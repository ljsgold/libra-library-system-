package com.libra.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.libra.admin.dto.BorrowDTO;
import com.libra.admin.entity.LibBorrowRecord;
import com.libra.admin.entity.LibInventory;
import com.libra.admin.mapper.LibBookMapper;
import com.libra.admin.mapper.LibBorrowRecordMapper;
import com.libra.admin.mapper.LibInventoryMapper;
import com.libra.common.core.annotation.Log;
import com.libra.common.exception.BusinessException;
import com.libra.common.utils.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LibBorrowService {

    @Autowired
    private LibBorrowRecordMapper borrowRecordMapper;

    @Autowired
    private LibInventoryMapper inventoryMapper;

    @Autowired
    private LibBookMapper bookMapper;

    @Log(module = "借阅管理", operation = "借书")
    @Transactional(rollbackFor = Exception.class)
    public void borrowBook(BorrowDTO dto) {
        Long userId = dto.getUserId();
        Long bookId = dto.getBookId();

        // 0. 校验用户借阅资格 (示例：是否有超期未还)
        long overdueCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<LibBorrowRecord>()
                .eq(LibBorrowRecord::getUserId, userId)
                .eq(LibBorrowRecord::getStatus, 3)); // 3:已逾期
        if (overdueCount > 0) {
            throw new BusinessException("存在超期未还图书，请先处理");
        }

        // 1. 查找可用馆藏 (使用 select for update 保证原子性，或依赖数据库状态更新)
        LibInventory inventory = inventoryMapper.selectOne(new LambdaQueryWrapper<LibInventory>()
                .eq(LibInventory::getBookId, bookId)
                .eq(LibInventory::getStatus, 1) // 在馆
                .last("limit 1 for update"));

        if (inventory == null) {
            syncBookCounts(bookId);
            throw new BusinessException("该图书当前无可用库存");
        }

        // 2. 更新馆藏状态为借出
        inventory.setStatus(2);
        inventoryMapper.updateById(inventory);

        // 3. 更新图书主表在馆册数 (原子更新)
        int updateCount = bookMapper.updateStock(bookId, -1);
        if (updateCount == 0) {
            throw new BusinessException("库存扣减失败");
        }

        // 4. 创建借阅记录
        LibBorrowRecord record = new LibBorrowRecord();
        record.setTenantId(TenantContext.getTenantId());
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setInventoryId(inventory.getId());
        record.setBorrowTime(LocalDateTime.now());
        record.setDueTime(LocalDateTime.now().plusDays(30)); // 默认借阅30天
        record.setStatus(1); // 借阅中
        borrowRecordMapper.insert(record);
    }

    @Log(module = "借阅管理", operation = "还书")
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long inventoryId) {
        // 1. 查找借阅记录
        LibBorrowRecord record = borrowRecordMapper.selectOne(new LambdaQueryWrapper<LibBorrowRecord>()
                .eq(LibBorrowRecord::getInventoryId, inventoryId)
                .in(LibBorrowRecord::getStatus, 1, 3)
                .orderByDesc(LibBorrowRecord::getBorrowTime)
                .last("limit 1"));

        if (record == null) {
            throw new BusinessException("未找到该馆藏的借阅记录");
        }

        // 2. 更新借阅记录状态
        record.setStatus(2); // 已归还
        record.setReturnTime(LocalDateTime.now());
        borrowRecordMapper.updateById(record);

        // 3. 更新馆藏状态为在馆
        LibInventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new BusinessException("馆藏不存在");
        }
        inventory.setStatus(1);
        inventoryMapper.updateById(inventory);

        // 4. 更新图书主表在馆册数
        int updateCount = bookMapper.updateStock(record.getBookId(), 1);
        if (updateCount == 0) {
            throw new BusinessException("库存回滚失败");
        }
    }

    private void syncBookCounts(Long bookId) {
        long total = inventoryMapper.selectCount(new LambdaQueryWrapper<LibInventory>()
                .eq(LibInventory::getBookId, bookId));
        long available = inventoryMapper.selectCount(new LambdaQueryWrapper<LibInventory>()
                .eq(LibInventory::getBookId, bookId)
                .eq(LibInventory::getStatus, 1));

        com.libra.admin.entity.LibBook book = bookMapper.selectById(bookId);
        if (book == null) {
            return;
        }
        book.setTotalCount((int) total);
        book.setStockCount((int) available);
        bookMapper.updateById(book);
    }
}
