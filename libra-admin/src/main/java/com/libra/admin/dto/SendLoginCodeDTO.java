package com.libra.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendLoginCodeDTO {
    @NotBlank(message = "username is required")
    private String username;
}
