package com.libra.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libra.admin.dto.BookAddDTO;
import com.libra.admin.entity.LibBook;
import com.libra.admin.service.LibBookService;
import com.libra.common.core.annotation.RateLimiter;
import com.libra.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
@Tag(name = "图书管理", description = "图书增删改查接口")
public class BookController {

    @Autowired
    private LibBookService bookService;

    @RateLimiter(count = 20, time = 60)
    @PostMapping("/add")
    @Operation(summary = "新增图书")
    public R<Void> addBook(@Validated @RequestBody BookAddDTO dto) {
        bookService.addBook(dto);
        return R.ok();
    }

    @GetMapping("/list")
    @Operation(summary = "图书列表")
    public R<Page<LibBook>> listBooks(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title) {
        return R.ok(bookService.listBooks(pageNum, pageSize, title));
    }
}
