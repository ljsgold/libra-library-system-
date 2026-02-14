package com.libra.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.libra.admin.dto.*;
import com.libra.admin.entity.SysUser;
import com.libra.admin.mapper.SysUserMapper;
import com.libra.common.exception.BusinessException;
import com.libra.common.utils.TenantContext;
import com.libra.framework.security.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {

    private static final long DEFAULT_TENANT_ID = 1L;
    private static final int CODE_EXPIRE_MINUTES = 10;

    private final Map<String, CodeCache> loginCodes = new ConcurrentHashMap<>();
    private final Map<String, CodeCache> resetCodes = new ConcurrentHashMap<>();

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private com.libra.admin.config.WechatConfig wechatConfig;

    @Autowired
    private org.springframework.web.client.RestTemplate restTemplate;

    // 微信开放平台网站应用扫码登录授权地址（PC端使用）
    private static final String WECHAT_QR_CONNECT_URL = "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_login&state=%s#wechat_redirect";
    // 微信获取 access_token 和 openid 的地址 (通用)
    private static final String WECHAT_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    public String getWechatLoginUrl() {
        // 如果微信配置未启用（AppID为空或为默认值），返回null
        String appid = wechatConfig.getAppid();
        if (appid == null || appid.isEmpty() || "your_appid".equals(appid)) {
            return null;
        }
        
        String secret = wechatConfig.getSecret();
        if (secret == null || secret.isEmpty() || "your_secret".equals(secret)) {
            return null;
        }
        
        String redirectUri = wechatConfig.getRedirectUri();
        if (redirectUri == null || redirectUri.isEmpty()) {
            throw new BusinessException("微信回调地址未配置，请在配置文件中设置 wechat.redirect-uri");
        }
        
        try {
            // 微信要求 redirect_uri 必须进行 URL 编码
            String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
            String state = String.valueOf(System.currentTimeMillis());
            // 使用网站应用扫码登录URL，支持PC端扫码登录
            return String.format(WECHAT_QR_CONNECT_URL, appid, encodedRedirectUri, state);
        } catch (Exception e) {
            throw new BusinessException("生成微信登录URL失败: " + e.getMessage());
        }
    }

    @Transactional
    public Map<String, String> wechatLogin(String code) {
        // 检查微信配置是否启用
        String appid = wechatConfig.getAppid();
        String secret = wechatConfig.getSecret();
        if (appid == null || appid.isEmpty() || "your_appid".equals(appid) || 
            secret == null || secret.isEmpty() || "your_secret".equals(secret)) {
            throw new BusinessException("微信登录功能未启用");
        }
        if (code == null || code.isEmpty()) {
            throw new BusinessException("微信授权码不能为空");
        }
        // 1. 调用微信接口获取 openid
        String url = String.format(WECHAT_TOKEN_URL, appid, secret, code);
        WechatTokenResponse response = restTemplate.getForObject(url, WechatTokenResponse.class);
        
        if (response == null) {
            throw new BusinessException("微信登录失败: 无法获取用户信息");
        }
        
        if (response.getOpenid() == null) {
            String errorMsg = response.getErrmsg() != null ? response.getErrmsg() : "未知错误";
            throw new BusinessException("微信登录失败: " + errorMsg);
        }
        
        String openid = response.getOpenid();

        // 2. 根据 openid 查询用户
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getWechatOpenid, openid));

        if (user == null) {
            // 3. 如果用户不存在，可以自动注册一个新用户
            user = new SysUser();
            user.setTenantId(DEFAULT_TENANT_ID);
            user.setUsername("wx_" + code);
            user.setPassword(passwordEncoder.encode("123456")); // 默认密码
            user.setNickname("微信用户");
            user.setWechatOpenid(openid);
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);
        }

        // 4. 生成 Token
        String accessToken = jwtUtils.createToken(user.getId(), user.getTenantId(), user.getUsername());
        String refreshToken = jwtUtils.createRefreshToken(user.getId(), user.getTenantId(), user.getUsername());

        Map<String, String> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        Long tenantId = dto.getTenantId() != null ? dto.getTenantId() : DEFAULT_TENANT_ID;
        TenantContext.setTenantId(tenantId);

        long count = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setTenantId(tenantId);
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getName());
        user.setStatus(1);
        userMapper.insert(user);
    }

    public Map<String, String> login(LoginDTO loginDTO) {
        Long tenantId = loginDTO.getTenantId() != null ? loginDTO.getTenantId() : DEFAULT_TENANT_ID;
        TenantContext.setTenantId(tenantId);

        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, loginDTO.getUsername()));

        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (Objects.equals(user.getStatus(), 0)) {
            throw new BusinessException("账号已被禁用");
        }

        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        return buildTokens(user);
    }

    public Map<String, String> loginByCode(LoginCodeDTO dto) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user != null) {
            TenantContext.setTenantId(user.getTenantId() != null ? user.getTenantId() : DEFAULT_TENANT_ID);
        }
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        validateCode(loginCodes, dto.getUsername(), dto.getCode());
        if (Objects.equals(user.getStatus(), 0)) {
            throw new BusinessException("账号已被禁用");
        }
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        return buildTokens(user);
    }

    public void sendLoginCode(SendLoginCodeDTO dto) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user != null) {
            TenantContext.setTenantId(user.getTenantId() != null ? user.getTenantId() : DEFAULT_TENANT_ID);
        }
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        String code = generateCode();
        loginCodes.put(dto.getUsername(), new CodeCache(code, LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES)));
        System.out.println("[LoginCode] user=" + dto.getUsername() + " code=" + code);
    }

    public void sendResetCode(ForgotCodeDTO dto) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user != null) {
            TenantContext.setTenantId(user.getTenantId() != null ? user.getTenantId() : DEFAULT_TENANT_ID);
        }
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(dto.getEmail())) {
            throw new BusinessException("邮箱不匹配");
        }
        String code = generateCode();
        resetCodes.put(dto.getUsername(), new CodeCache(code, LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES)));
        System.out.println("[ResetCode] user=" + dto.getUsername() + " code=" + code);
    }

    public void resetPassword(ResetPasswordDTO dto) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user != null) {
            TenantContext.setTenantId(user.getTenantId() != null ? user.getTenantId() : DEFAULT_TENANT_ID);
        }
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(dto.getEmail())) {
            throw new BusinessException("邮箱不匹配");
        }
        validateCode(resetCodes, dto.getUsername(), dto.getCode());
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);
    }

    public Map<String, String> refresh(String refreshToken) {
        try {
            Claims claims = jwtUtils.parseToken(refreshToken);
            Long userId = claims.get("userId", Long.class);
            Long tenantId = claims.get("tenantId", Long.class);
            String username = claims.getSubject();

            String accessToken = jwtUtils.createToken(userId, tenantId, username);
            String newRefreshToken = jwtUtils.createRefreshToken(userId, tenantId, username);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", newRefreshToken);
            return tokens;
        } catch (Exception e) {
            throw new BusinessException(401, "令牌无效或已过期");
        }
    }

    private Map<String, String> buildTokens(SysUser user) {
        Long tenantId = user.getTenantId() != null ? user.getTenantId() : DEFAULT_TENANT_ID;
        String accessToken = jwtUtils.createToken(user.getId(), tenantId, user.getUsername());
        String refreshToken = jwtUtils.createRefreshToken(user.getId(), tenantId, user.getUsername());
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    private void validateCode(Map<String, CodeCache> cache, String username, String code) {
        CodeCache stored = cache.get(username);
        if (stored == null || stored.expireAt.isBefore(LocalDateTime.now()) || !stored.code.equals(code)) {
            throw new BusinessException("验证码无效或已过期");
        }
        cache.remove(username);
    }

    private String generateCode() {
        int val = ThreadLocalRandom.current().nextInt(100000, 999999);
        return String.valueOf(val);
    }

    private static class CodeCache {
        private final String code;
        private final LocalDateTime expireAt;

        private CodeCache(String code, LocalDateTime expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }
}
