package com.libra.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.libra.admin.domain.borrow.BorrowStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借阅记录实体
 * 包含领域方法，让实体更"聪明"
 */
@Data
@TableName("lib_borrow_record")
public class LibBorrowRecord {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long tenantId;
    private Long inventoryId;
    private Long bookId;
    private Long userId;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private Integer status; // 1:借阅中, 2:已归还, 3:已逾期, 4:已赔付
    private BigDecimal fineAmount;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // ========== 领域方法 ==========
    
    /**
     * 获取借阅状态枚举
     */
    public BorrowStatus getBorrowStatus() {
        return status != null ? BorrowStatus.fromCode(status) : null;
    }
    
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
     * 判断是否为借阅中或逾期状态（可归还）
     */
    public boolean isBorrowingOrOverdue() {
        BorrowStatus borrowStatus = getBorrowStatus();
        return borrowStatus != null && borrowStatus.isBorrowingOrOverdue();
    }
    
    /**
     * 标记为已归还
     * 
     * @param returnTime 归还时间
     */
    public void markReturned(LocalDateTime returnTime) {
        this.returnTime = returnTime;
        this.status = BorrowStatus.RETURNED.getCode();
    }
    
    /**
     * 标记为已逾期
     */
    public void markOverdue() {
        this.status = BorrowStatus.OVERDUE.getCode();
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
}
