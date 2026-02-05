package com.libra.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "图书ID不能为空")
    private Long bookId;
}
