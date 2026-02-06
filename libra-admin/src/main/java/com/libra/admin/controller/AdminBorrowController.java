package com.libra.admin.controller;

import com.libra.admin.mapper.LibBorrowRecordMapper;
import com.libra.admin.vo.AdminBorrowRecordVO;
import com.libra.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/borrow")
@Tag(name = "借阅管理", description = "管理员借阅记录接口")
public class AdminBorrowController {

    @Autowired
    private LibBorrowRecordMapper borrowRecordMapper;

    @GetMapping("/records")
    @Operation(summary = "借阅记录列表")
    public R<List<AdminBorrowRecordVO>> records(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "50") int limit) {
        int safeLimit = limit <= 0 ? 50 : Math.min(limit, 200);
        List<AdminBorrowRecordVO> records;
        if (status == null) {
            records = borrowRecordMapper.selectBorrowRecords(safeLimit);
        } else {
            records = borrowRecordMapper.selectBorrowRecordsByStatus(status, safeLimit);
        }
        return R.ok(records);
    }
}
