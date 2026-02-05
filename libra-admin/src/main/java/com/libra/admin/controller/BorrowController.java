package com.libra.admin.controller;

import com.libra.admin.dto.BorrowDTO;
import com.libra.admin.service.LibBorrowService;
import com.libra.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private LibBorrowService borrowService;

    @PostMapping("/do")
    public R<Void> borrowBook(@Validated @RequestBody BorrowDTO dto) {
        borrowService.borrowBook(dto);
        return R.ok();
    }

    @PostMapping("/return")
    public R<Void> returnBook(@RequestParam Long inventoryId) {
        borrowService.returnBook(inventoryId);
        return R.ok();
    }
}
