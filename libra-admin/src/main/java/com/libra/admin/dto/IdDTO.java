package com.libra.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IdDTO {
    @NotNull(message = "id is required")
    private Long id;
}
