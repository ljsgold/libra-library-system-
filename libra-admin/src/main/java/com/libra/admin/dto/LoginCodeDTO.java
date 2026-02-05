package com.libra.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginCodeDTO {
    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "code is required")
    private String code;
}
