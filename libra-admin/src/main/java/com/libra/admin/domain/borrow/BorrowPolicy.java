package com.libra.admin.domain.borrow;

/**
 * 借阅策略接口
 * 定义借阅规则：借阅天数、最大借阅数量等
 * 
 * 未来可以扩展为：
 * - 不同用户类型（学生/老师/外来人员）不同策略
 * - 不同图书类型（新书/畅销书/普通书）不同策略
 * - 可配置的策略（从数据库或配置文件读取）
 */
public interface BorrowPolicy {
    
    /**
     * 获取默认借阅天数
     * 
     * @param userId 用户ID（可用于查询用户类型）
     * @param bookId 图书ID（可用于查询图书类型）
     * @return 借阅天数
     */
    int getBorrowDays(Long userId, Long bookId);
    
    /**
     * 获取最大借阅数量
     * 
     * @param userId 用户ID
     * @return 最大借阅数量
     */
    int getMaxBorrowCount(Long userId);
    
    /**
     * 是否允许续借
     * 
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 是否允许续借
     */
    boolean isRenewalAllowed(Long userId, Long bookId);
    
    /**
     * 获取续借天数
     * 
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 续借天数
     */
    default int getRenewalDays(Long userId, Long bookId) {
        return isRenewalAllowed(userId, bookId) ? getBorrowDays(userId, bookId) : 0;
    }
}
