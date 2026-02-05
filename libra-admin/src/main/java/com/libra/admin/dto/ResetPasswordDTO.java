package com.libra.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordDTO {
    @NotBlank(message = "username is required")
    private String username;

    @Email(message = "email invalid")
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "code is required")
    private String code;

    @NotBlank(message = "newPassword is required")
    private String newPassword;
}
