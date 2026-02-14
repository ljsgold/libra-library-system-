package com.libra.admin.domain.borrow;

import org.springframework.stereotype.Component;

/**
 * 默认借阅策略实现
 * 
 * 当前实现为固定规则，后续可以：
 * - 从 lib_config 表读取配置
 * - 根据用户类型（SysUser 扩展字段）动态计算
 * - 根据图书分类动态计算
 */
@Component
public class DefaultBorrowPolicy implements BorrowPolicy {
    
    /**
     * 默认借阅天数：30天
     */
    private static final int DEFAULT_BORROW_DAYS = 30;
    
    /**
     * 默认最大借阅数量：5本
     */
    private static final int DEFAULT_MAX_BORROW_COUNT = 5;
    
    @Override
    public int getBorrowDays(Long userId, Long bookId) {
        // TODO: 未来可以根据用户类型、图书类型返回不同天数
        // 例如：学生30天，老师60天，新书15天等
        return DEFAULT_BORROW_DAYS;
    }
    
    @Override
    public int getMaxBorrowCount(Long userId) {
        // TODO: 未来可以根据用户类型返回不同数量
        // 例如：学生5本，老师10本等
        return DEFAULT_MAX_BORROW_COUNT;
    }
    
    @Override
    public boolean isRenewalAllowed(Long userId, Long bookId) {
        // TODO: 未来可以实现续借规则
        // 例如：只能续借一次、有逾期不能续借等
        return true;
    }
}
