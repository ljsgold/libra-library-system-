package com.libra.admin.domain.borrow;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.libra.admin.entity.LibBorrowRecord;
import com.libra.admin.entity.LibInventory;
import com.libra.admin.mapper.LibBookMapper;
import com.libra.admin.mapper.LibBorrowRecordMapper;
import com.libra.admin.mapper.LibInventoryMapper;
import com.libra.common.core.constant.ResultCode;
import com.libra.common.exception.BusinessException;
import com.libra.common.utils.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 借阅领域服务
 * 
 * 职责：
 * - 封装借阅业务规则和流程编排
 * - 协调多个实体完成复杂业务逻辑
 * - 不负责事务管理（由应用服务层负责）
 * 
 * 设计原则：
 * - 领域服务应该是无状态的
 * - 领域服务中的方法应该表达业务意图
 * - 领域服务不应该直接依赖基础设施层（但当前为了简化，暂时直接使用 Mapper）
 */
@Component
public class BorrowDomainService {
    
    @Autowired
    private LibBorrowRecordMapper borrowRecordMapper;
    
    @Autowired
    private LibInventoryMapper inventoryMapper;
    
    @Autowired
    private LibBookMapper bookMapper;
    
    @Autowired
    private BorrowPolicy borrowPolicy;
    
    /**
     * 借书
     * 
     * @param userId 用户ID
     * @param bookId 图书ID
     */
    public void borrowBook(Long userId, Long bookId) {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. 校验用户借阅资格（是否有逾期未还）
        validateBorrowEligibility(userId);
        
        // 2. 校验是否达到最大借阅数量
        validateBorrowLimit(userId);
        
        // 3. 查找可用馆藏（使用 select for update 保证原子性）
        LibInventory inventory = findAvailableInventory(bookId);
        
        // 4. 更新馆藏状态为借出
        inventory.setStatus(2); // 借出
        inventoryMapper.updateById(inventory);
        
        // 5. 更新图书主表在馆册数（原子更新）
        int updateCount = bookMapper.updateStock(bookId, -1);
        if (updateCount == 0) {
            throw new BusinessException(ResultCode.STOCK_DECREASE_FAILED);
        }
        
        // 6. 创建借阅记录
        createBorrowRecord(userId, bookId, inventory.getId(), now);
    }
    
    /**
     * 还书
     * 
     * @param inventoryId 馆藏ID
     */
    public void returnBook(Long inventoryId) {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. 查找借阅记录
        LibBorrowRecord record = findActiveBorrowRecord(inventoryId);
        
        // 2. 幂等性检查：如果已归还，直接返回
        if (record.getBorrowStatus() == BorrowStatus.RETURNED) {
            return; // 已归还，幂等处理
        }
        
        // 3. 更新借阅记录状态
        record.markReturned(now);
        borrowRecordMapper.updateById(record);
        
        // 4. 更新馆藏状态为在馆
        LibInventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new BusinessException(ResultCode.INVENTORY_NOT_FOUND);
        }
        inventory.setStatus(1); // 在馆
        inventoryMapper.updateById(inventory);
        
        // 5. 更新图书主表在馆册数
        int updateCount = bookMapper.updateStock(record.getBookId(), 1);
        if (updateCount == 0) {
            throw new BusinessException(ResultCode.STOCK_INCREASE_FAILED);
        }
    }
    
    /**
     * 校验用户借阅资格（是否有逾期未还）
     */
    private void validateBorrowEligibility(Long userId) {
        long overdueCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<LibBorrowRecord>()
                .eq(LibBorrowRecord::getUserId, userId)
                .eq(LibBorrowRecord::getStatus, BorrowStatus.OVERDUE.getCode()));
        if (overdueCount > 0) {
            throw new BusinessException(ResultCode.HAS_OVERDUE_BOOKS);
        }
    }
    
    /**
     * 校验是否达到最大借阅数量
     */
    private void validateBorrowLimit(Long userId) {
        int maxCount = borrowPolicy.getMaxBorrowCount(userId);
        long currentCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<LibBorrowRecord>()
                .eq(LibBorrowRecord::getUserId, userId)
                .in(LibBorrowRecord::getStatus, 
                    BorrowStatus.BORROWING.getCode(), 
                    BorrowStatus.OVERDUE.getCode()));
        if (currentCount >= maxCount) {
            throw new BusinessException(ResultCode.BORROW_LIMIT_REACHED);
        }
    }
    
    /**
     * 查找可用馆藏
     */
    private LibInventory findAvailableInventory(Long bookId) {
        LibInventory inventory = inventoryMapper.selectOne(new LambdaQueryWrapper<LibInventory>()
                .eq(LibInventory::getBookId, bookId)
                .eq(LibInventory::getStatus, 1) // 在馆
                .last("limit 1 for update"));
        
        if (inventory == null) {
            // 尝试同步库存数据
            syncBookCounts(bookId);
            throw new BusinessException(ResultCode.INVENTORY_NOT_AVAILABLE);
        }
        
        return inventory;
    }
    
    /**
     * 创建借阅记录
     */
    private void createBorrowRecord(Long userId, Long bookId, Long inventoryId, LocalDateTime borrowTime) {
        int borrowDays = borrowPolicy.getBorrowDays(userId, bookId);
        
        LibBorrowRecord record = new LibBorrowRecord();
        record.setTenantId(TenantContext.getTenantId());
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setInventoryId(inventoryId);
        record.setBorrowTime(borrowTime);
        record.setDueTime(borrowTime.plusDays(borrowDays));
        record.setStatus(BorrowStatus.BORROWING.getCode());
        borrowRecordMapper.insert(record);
    }
    
    /**
     * 查找活跃的借阅记录（借阅中或逾期）
     */
    private LibBorrowRecord findActiveBorrowRecord(Long inventoryId) {
        LibBorrowRecord record = borrowRecordMapper.selectOne(new LambdaQueryWrapper<LibBorrowRecord>()
                .eq(LibBorrowRecord::getInventoryId, inventoryId)
                .in(LibBorrowRecord::getStatus, 
                    BorrowStatus.BORROWING.getCode(), 
                    BorrowStatus.OVERDUE.getCode())
                .orderByDesc(LibBorrowRecord::getBorrowTime)
                .last("limit 1"));
        
        if (record == null) {
            throw new BusinessException(ResultCode.BORROW_RECORD_NOT_FOUND);
        }
        
        return record;
    }
    
    /**
     * 同步图书库存数据（当库存不一致时调用）
     */
    private void syncBookCounts(Long bookId) {
        long total = inventoryMapper.selectCount(new LambdaQueryWrapper<LibInventory>()
                .eq(LibInventory::getBookId, bookId));
        long available = inventoryMapper.selectCount(new LambdaQueryWrapper<LibInventory>()
                .eq(LibInventory::getBookId, bookId)
                .eq(LibInventory::getStatus, 1));
        
        com.libra.admin.entity.LibBook book = bookMapper.selectById(bookId);
        if (book != null) {
            book.setTotalCount((int) total);
            book.setStockCount((int) available);
            bookMapper.updateById(book);
        }
    }
}
