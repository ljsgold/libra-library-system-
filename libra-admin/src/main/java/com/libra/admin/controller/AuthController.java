package com.libra.admin.controller;

import com.libra.admin.dto.*;
import com.libra.admin.service.AuthService;
import com.libra.common.core.domain.R;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return R.ok();
    }

    @PostMapping("/login")
    public R<Map<String, String>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return R.ok(authService.login(loginDTO));
    }

    @PostMapping("/login/code")
    public R<Void> sendLoginCode(@Valid @RequestBody SendLoginCodeDTO dto) {
        authService.sendLoginCode(dto);
        return R.ok();
    }

    @PostMapping("/login/by-code")
    public R<Map<String, String>> loginByCode(@Valid @RequestBody LoginCodeDTO dto) {
        return R.ok(authService.loginByCode(dto));
    }

    @PostMapping("/forgot/code")
    public R<Void> sendForgotCode(@Valid @RequestBody ForgotCodeDTO dto) {
        authService.sendResetCode(dto);
        return R.ok();
    }

    @PostMapping("/forgot/reset")
    public R<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        authService.resetPassword(dto);
        return R.ok();
    }

    @PostMapping("/refresh")
    public R<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        return R.ok(authService.refresh(refreshToken));
    }
}
