package com.libra.admin.controller;

import com.libra.admin.dto.BorrowDTO;
import com.libra.admin.service.LibBorrowService;
import com.libra.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/borrow")
@Tag(name = "借阅操作", description = "借阅与归还接口")
public class BorrowController {

    @Autowired
    private LibBorrowService borrowService;

    @PostMapping("/do")
    @Operation(summary = "借阅图书")
    public R<Void> borrowBook(@Validated @RequestBody BorrowDTO dto) {
        borrowService.borrowBook(dto);
        return R.ok();
    }

    @PostMapping("/return")
    @Operation(summary = "归还图书")
    public R<Void> returnBook(@RequestParam Long inventoryId) {
        borrowService.returnBook(inventoryId);
        return R.ok();
    }
}
