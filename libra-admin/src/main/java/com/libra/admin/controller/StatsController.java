package com.libra.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.libra.admin.entity.LibBorrowRecord;
import com.libra.admin.mapper.LibBookMapper;
import com.libra.admin.mapper.LibBorrowRecordMapper;
import com.libra.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stats")
@Tag(name = "统计", description = "统计相关接口")
public class StatsController {

    @Autowired
    private LibBookMapper bookMapper;

    @Autowired
    private LibBorrowRecordMapper borrowRecordMapper;

    @GetMapping("/dashboard")
    @Operation(summary = "仪表盘统计")
    public R<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总图书种类
        stats.put("totalBookTypes", bookMapper.selectCount(null));
        
        // 总馆藏册数
        stats.put("totalInventory", bookMapper.selectSumTotalCount());
        
        // 当前借出册数
        long borrowingCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<LibBorrowRecord>()
                .eq(LibBorrowRecord::getStatus, 1));
        stats.put("borrowingCount", borrowingCount);
        
        // 逾期册数
        long overdueCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<LibBorrowRecord>()
                .eq(LibBorrowRecord::getStatus, 3));
        stats.put("overdueCount", overdueCount);

        // 借阅趋势 (最近7天)
        stats.put("borrowTrend", borrowRecordMapper.selectDailyBorrowTrend());
        
        return R.ok(stats);
    }
}
