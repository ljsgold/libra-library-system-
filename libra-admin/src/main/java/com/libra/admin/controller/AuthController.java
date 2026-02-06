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
@Tag(name = "认证", description = "登录注册相关接口")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public R<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return R.ok();
    }

    @PostMapping("/login")
    @Operation(summary = "账号密码登录")
    public R<Map<String, String>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return R.ok(authService.login(loginDTO));
    }

    @PostMapping("/login/code")
    @Operation(summary = "发送登录验证码")
    public R<Void> sendLoginCode(@Valid @RequestBody SendLoginCodeDTO dto) {
        authService.sendLoginCode(dto);
        return R.ok();
    }

    @PostMapping("/login/by-code")
    @Operation(summary = "验证码登录")
    public R<Map<String, String>> loginByCode(@Valid @RequestBody LoginCodeDTO dto) {
        return R.ok(authService.loginByCode(dto));
    }

    @PostMapping("/forgot/code")
    @Operation(summary = "发送重置密码验证码")
    public R<Void> sendForgotCode(@Valid @RequestBody ForgotCodeDTO dto) {
        authService.sendResetCode(dto);
        return R.ok();
    }

    @PostMapping("/forgot/reset")
    @Operation(summary = "重置密码")
    public R<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        authService.resetPassword(dto);
        return R.ok();
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌")
    public R<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        return R.ok(authService.refresh(refreshToken));
    }
}
