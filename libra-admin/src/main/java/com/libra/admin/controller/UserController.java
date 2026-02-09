package com.libra.admin.controller;

import com.libra.admin.dto.BookIdDTO;
import com.libra.admin.dto.IdDTO;
import com.libra.admin.service.UserPortalService;
import com.libra.admin.vo.*;
import com.libra.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "用户端", description = "用户门户相关接口")
public class UserController {

    @Autowired
    private UserPortalService userPortalService;

    @GetMapping("/home/new-books")
    @Operation(summary = "最新上架")
    public R<List<BookItemVO>> getNewBooks() {
        return R.ok(userPortalService.getNewBooks(8));
    }

    @GetMapping("/home/popular-books")
    @Operation(summary = "热门图书")
    public R<List<BookItemVO>> getPopularBooks() {
        return R.ok(userPortalService.getPopularBooks(8));
    }

    @GetMapping("/categories")
    @Operation(summary = "图书分类")
    public R<List<CategoryItemVO>> getCategories() {
        return R.ok(userPortalService.getCategories());
    }

    @GetMapping("/books")
    @Operation(summary = "图书搜索")
    public R<PageResult<BookItemVO>> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(userPortalService.searchBooks(keyword, categoryId, onlyAvailable, sort, page, size));
    }

    @GetMapping("/books/{id}")
    @Operation(summary = "图书详情")
    public R<BookDetailVO> getBookDetail(@PathVariable Long id) {
        return R.ok(userPortalService.getBookDetail(id));
    }

    @PostMapping("/borrow")
    @Operation(summary = "借阅图书")
    public R<Void> borrow(@Valid @RequestBody BookIdDTO dto) {
        userPortalService.borrowBook(dto.getBookId());
        return R.ok();
    }

    @PostMapping("/reserve")
    @Operation(summary = "预约图书")
    public R<Void> reserve(@Valid @RequestBody BookIdDTO dto) {
        userPortalService.reserveBook(dto.getBookId());
        return R.ok();
    }

    @GetMapping("/my-borrow/current")
    @Operation(summary = "当前借阅")
    public R<List<BorrowRecordVO>> getCurrentBorrow() {
        return R.ok(userPortalService.getCurrentBorrowList());
    }

    @GetMapping("/my-borrow/history")
    @Operation(summary = "历史借阅")
    public R<List<BorrowRecordVO>> getHistoryBorrow() {
        return R.ok(userPortalService.getHistoryBorrowList());
    }

    @PostMapping("/my-borrow/renew")
    @Operation(summary = "续借")
    public R<Void> renew(@Valid @RequestBody IdDTO dto) {
        userPortalService.renewBorrow(dto.getId());
        return R.ok();
    }

    @PostMapping("/my-borrow/return")
    @Operation(summary = "归还")
    public R<Void> returnBorrow(@Valid @RequestBody IdDTO dto) {
        userPortalService.returnBorrow(dto.getId());
        return R.ok();
    }

    @GetMapping("/profile")
    @Operation(summary = "个人信息")
    public R<UserProfileVO> profile() {
        return R.ok(userPortalService.getProfile());
    }

    @GetMapping("/borrow-rules")
    @Operation(summary = "借阅规则")
    public R<List<BorrowRuleItemVO>> borrowRules() {
        return R.ok(userPortalService.getBorrowRules());
    }

    @GetMapping("/reservations")
    @Operation(summary = "预约记录")
    public R<List<ReservationRecordVO>> reservations() {
        return R.ok(userPortalService.getReservationList());
    }

    @PostMapping("/reservations/cancel")
    @Operation(summary = "取消预约")
    public R<Void> cancelReservation(@Valid @RequestBody IdDTO dto) {
        userPortalService.cancelReservation(dto.getId());
        return R.ok();
    }

    @PostMapping("/reservations/subscribe")
    @Operation(summary = "到书提醒订阅")
    public R<Void> subscribeReservation(@Valid @RequestBody IdDTO dto) {
        userPortalService.subscribeArrival(dto.getId());
        return R.ok();
    }
}
