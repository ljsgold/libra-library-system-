package com.libra.admin.service;

import com.libra.admin.domain.borrow.BorrowDomainService;
import com.libra.admin.dto.BorrowDTO;
import com.libra.common.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 借阅应用服务
 * 
 * 职责：
 * - 事务边界控制（@Transactional）
 * - 审计日志（@Log）
 * - 调用领域服务完成业务逻辑
 * - 不包含业务规则（业务规则在 BorrowDomainService 中）
 */
@Service
public class LibBorrowService {

    @Autowired
    private BorrowDomainService borrowDomainService;

    /**
     * 借书
     * 
     * @param dto 借阅DTO
     */
    @Log(module = "借阅管理", operation = "借书")
    @Transactional(rollbackFor = Exception.class)
    public void borrowBook(BorrowDTO dto) {
        borrowDomainService.borrowBook(dto.getUserId(), dto.getBookId());
    }

    /**
     * 还书
     * 
     * @param inventoryId 馆藏ID
     */
    @Log(module = "借阅管理", operation = "还书")
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long inventoryId) {
        borrowDomainService.returnBook(inventoryId);
    }
}
