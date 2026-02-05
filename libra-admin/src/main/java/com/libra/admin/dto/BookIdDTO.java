package com.libra.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookIdDTO {
    @NotNull(message = "bookId is required")
    private Long bookId;
}
