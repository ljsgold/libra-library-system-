package com.libra.admin.controller;

import com.libra.admin.dto.BookIdDTO;
import com.libra.admin.dto.IdDTO;
import com.libra.admin.service.UserPortalService;
import com.libra.admin.vo.*;
import com.libra.common.core.domain.R;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserPortalService userPortalService;

    @GetMapping("/home/new-books")
    public R<List<BookItemVO>> getNewBooks() {
        return R.ok(userPortalService.getNewBooks(8));
    }

    @GetMapping("/home/popular-books")
    public R<List<BookItemVO>> getPopularBooks() {
        return R.ok(userPortalService.getPopularBooks(8));
    }

    @GetMapping("/categories")
    public R<List<CategoryItemVO>> getCategories() {
        return R.ok(userPortalService.getCategories());
    }

    @GetMapping("/books")
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
    public R<BookDetailVO> getBookDetail(@PathVariable Long id) {
        return R.ok(userPortalService.getBookDetail(id));
    }

    @PostMapping("/borrow")
    public R<Void> borrow(@Valid @RequestBody BookIdDTO dto) {
        userPortalService.borrowBook(dto.getBookId());
        return R.ok();
    }

    @PostMapping("/reserve")
    public R<Void> reserve(@Valid @RequestBody BookIdDTO dto) {
        userPortalService.reserveBook(dto.getBookId());
        return R.ok();
    }

    @GetMapping("/my-borrow/current")
    public R<List<BorrowRecordVO>> getCurrentBorrow() {
        return R.ok(userPortalService.getCurrentBorrowList());
    }

    @GetMapping("/my-borrow/history")
    public R<List<BorrowRecordVO>> getHistoryBorrow() {
        return R.ok(userPortalService.getHistoryBorrowList());
    }

    @PostMapping("/my-borrow/renew")
    public R<Void> renew(@Valid @RequestBody IdDTO dto) {
        userPortalService.renewBorrow(dto.getId());
        return R.ok();
    }

    @GetMapping("/profile")
    public R<UserProfileVO> profile() {
        return R.ok(userPortalService.getProfile());
    }

    @GetMapping("/borrow-rules")
    public R<List<BorrowRuleItemVO>> borrowRules() {
        return R.ok(userPortalService.getBorrowRules());
    }

    @GetMapping("/reservations")
    public R<List<ReservationRecordVO>> reservations() {
        return R.ok(userPortalService.getReservationList());
    }

    @PostMapping("/reservations/cancel")
    public R<Void> cancelReservation(@Valid @RequestBody IdDTO dto) {
        userPortalService.cancelReservation(dto.getId());
        return R.ok();
    }

    @PostMapping("/reservations/subscribe")
    public R<Void> subscribeReservation(@Valid @RequestBody IdDTO dto) {
        userPortalService.subscribeArrival(dto.getId());
        return R.ok();
    }
}
