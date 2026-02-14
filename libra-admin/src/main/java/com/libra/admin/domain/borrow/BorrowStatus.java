package com.libra.admin.domain.borrow;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 借阅状态枚举
 * 统一管理借阅记录的状态语义
 */
@Getter
@AllArgsConstructor
public enum BorrowStatus {
    /**
     * 借阅中
     */
    BORROWING(1, "借阅中"),
    
    /**
     * 已归还
     */
    RETURNED(2, "已归还"),
    
    /**
     * 已逾期
     */
    OVERDUE(3, "已逾期"),
    
    /**
     * 已赔付
     */
    COMPENSATED(4, "已赔付");

    private final int code;
    private final String description;

    /**
     * 根据代码获取状态枚举
     */
    public static BorrowStatus fromCode(int code) {
        for (BorrowStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的借阅状态代码: " + code);
    }

    /**
     * 判断是否为借阅中或逾期状态（可归还）
     */
    public boolean isBorrowingOrOverdue() {
        return this == BORROWING || this == OVERDUE;
    }
}
