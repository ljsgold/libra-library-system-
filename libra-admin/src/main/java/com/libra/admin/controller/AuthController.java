package com.libra.admin.controller;

import com.libra.admin.dto.*;
import com.libra.admin.service.AuthService;
import com.libra.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "璁よ瘉", description = "鐧诲綍娉ㄥ唽鐩稿叧鎺ュ彛")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "鐢ㄦ埛娉ㄥ唽")
    public R<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return R.ok();
    }

    @PostMapping("/login")
    @Operation(summary = "璐﹀彿瀵嗙爜鐧诲綍")
    public R<Map<String, String>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return R.ok(authService.login(loginDTO));
    }

    @PostMapping("/login/code")
    @Operation(summary = "鍙戦€佺櫥褰曢獙璇佺爜")
    public R<Void> sendLoginCode(@Valid @RequestBody SendLoginCodeDTO dto) {
        authService.sendLoginCode(dto);
        return R.ok();
    }

    @GetMapping("/wechat/url")
    @Operation(summary = "鑾峰彇寰俊鐧诲綍URL")
    public R<String> getWechatLoginUrl() {
        return R.ok(authService.getWechatLoginUrl());
    }

    @PostMapping("/wechat/login")
    @Operation(summary = "寰俊鍥炶皟鐧诲綍")
    public R<Map<String, String>> wechatLogin(@RequestParam String code) {
        return R.ok(authService.wechatLogin(code));
    }

    @PostMapping("/login/by-code")
    @Operation(summary = "验证码登录")
    public R<Map<String, String>> loginByCode(@Valid @RequestBody LoginCodeDTO dto) {
        return R.ok(authService.loginByCode(dto));
    }

    @PostMapping("/forgot/code")
    @Operation(summary = "鍙戦€侀噸缃瘑鐮侀獙璇佺爜")
    public R<Void> sendForgotCode(@Valid @RequestBody ForgotCodeDTO dto) {
        authService.sendResetCode(dto);
        return R.ok();
    }

    @PostMapping("/forgot/reset")
    @Operation(summary = "閲嶇疆瀵嗙爜")
    public R<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        authService.resetPassword(dto);
        return R.ok();
    }

    @PostMapping("/refresh")
    @Operation(summary = "鍒锋柊浠ょ墝")
    public R<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        return R.ok(authService.refresh(refreshToken));
    }
}

