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

    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        Long tenantId = dto.getTenantId() != null ? dto.getTenantId() : DEFAULT_TENANT_ID;
        TenantContext.setTenantId(tenantId);

        long count = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("username already exists");
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
            throw new BusinessException("invalid username or password");
        }

        if (Objects.equals(user.getStatus(), 0)) {
            throw new BusinessException("account disabled");
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
            throw new BusinessException("user not found");
        }
        validateCode(loginCodes, dto.getUsername(), dto.getCode());
        if (Objects.equals(user.getStatus(), 0)) {
            throw new BusinessException("account disabled");
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
            throw new BusinessException("user not found");
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
            throw new BusinessException("user not found");
        }
        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(dto.getEmail())) {
            throw new BusinessException("email not match");
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
            throw new BusinessException("user not found");
        }
        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(dto.getEmail())) {
            throw new BusinessException("email not match");
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
            throw new BusinessException(401, "token invalid or expired");
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
            throw new BusinessException("code invalid or expired");
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
