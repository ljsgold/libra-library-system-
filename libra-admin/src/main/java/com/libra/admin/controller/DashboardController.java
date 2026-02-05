package com.libra.admin.controller;

import com.libra.admin.service.DashboardService;
import com.libra.admin.vo.BorrowTrendVO;
import com.libra.admin.vo.DashboardOverviewVO;
import com.libra.admin.vo.HotBookVO;
import com.libra.admin.vo.OverdueRecordVO;
import com.libra.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/overview")
    public R<DashboardOverviewVO> overview() {
        return R.ok(dashboardService.getOverview());
    }

    @GetMapping("/borrow-trend")
    public R<List<BorrowTrendVO>> borrowTrend(@RequestParam(defaultValue = "7") int days) {
        return R.ok(dashboardService.getBorrowTrend(days));
    }

    @GetMapping("/hot-books")
    public R<List<HotBookVO>> hotBooks(@RequestParam(defaultValue = "5") int limit) {
        return R.ok(dashboardService.getHotBooks(limit));
    }

    @GetMapping("/overdue-records")
    public R<List<OverdueRecordVO>> overdueRecords(@RequestParam(defaultValue = "5") int limit) {
        return R.ok(dashboardService.getOverdueRecords(limit));
    }
}
