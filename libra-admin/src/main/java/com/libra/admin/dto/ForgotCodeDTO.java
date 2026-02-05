package com.libra.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotCodeDTO {
    @NotBlank(message = "username is required")
    private String username;

    @Email(message = "email invalid")
    @NotBlank(message = "email is required")
    private String email;
}
