package com.libra.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterDTO {
    private Long tenantId;

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "password must be 8+ chars with letters and numbers")
    private String password;

    @Email(message = "email invalid")
    private String email;

    private String phone;

    private String name;
}
