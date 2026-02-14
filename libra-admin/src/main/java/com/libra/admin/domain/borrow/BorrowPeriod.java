package com.libra.admin.domain.borrow;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * 借阅期限值对象
 * 封装借阅时间、应还时间、归还时间等时间相关逻辑
 * 
 * 值对象特点：
 * - 不可变（immutable）
 * - 包含业务逻辑（如计算是否逾期、逾期天数）
 * - 无标识符（通过属性值判断相等性）
 */
@Value
public class BorrowPeriod {
    
    /**
     * 借阅时间
     */
    LocalDateTime borrowTime;
    
    /**
     * 应还时间
     */
    LocalDateTime dueTime;
    
    /**
     * 归还时间（可能为null）
     */
    LocalDateTime returnTime;
    
    /**
     * 判断是否逾期
     * 
     * @param now 当前时间
     * @return 是否逾期
     */
    public boolean isOverdue(LocalDateTime now) {
        if (returnTime != null) {
            // 已归还，判断归还时是否逾期
            return returnTime.isAfter(dueTime);
        }
        // 未归还，判断当前是否逾期
        return now.isAfter(dueTime);
    }
    
    /**
     * 计算逾期天数
     * 
     * @param now 当前时间
     * @return 逾期天数（如果未逾期返回0）
     */
    public int calculateOverdueDays(LocalDateTime now) {
        if (!isOverdue(now)) {
            return 0;
        }
        LocalDateTime referenceTime = returnTime != null ? returnTime : now;
        long days = java.time.temporal.ChronoUnit.DAYS.between(dueTime, referenceTime);
        return (int) Math.max(0, days);
    }
    
    /**
     * 获取借阅天数
     * 
     * @return 借阅天数
     */
    public int getBorrowDays() {
        LocalDateTime endTime = returnTime != null ? returnTime : LocalDateTime.now();
        return (int) java.time.temporal.ChronoUnit.DAYS.between(borrowTime, endTime);
    }
    
    /**
     * 创建借阅期限
     * 
     * @param borrowTime 借阅时间
     * @param borrowDays 借阅天数
     * @return BorrowPeriod
     */
    public static BorrowPeriod create(LocalDateTime borrowTime, int borrowDays) {
        return new BorrowPeriod(borrowTime, borrowTime.plusDays(borrowDays), null);
    }
    
    /**
     * 标记为已归还
     * 
     * @param returnTime 归还时间
     * @return 新的 BorrowPeriod（值对象不可变，返回新实例）
     */
    public BorrowPeriod markReturned(LocalDateTime returnTime) {
        return new BorrowPeriod(borrowTime, dueTime, returnTime);
    }
}
