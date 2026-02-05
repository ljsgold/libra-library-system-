package com.libra.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.libra.admin.entity.LibBook;
import com.libra.admin.entity.LibBorrowRecord;
import com.libra.admin.mapper.LibBookMapper;
import com.libra.admin.mapper.LibBorrowRecordMapper;
import com.libra.admin.vo.BorrowTrendVO;
import com.libra.admin.vo.DashboardOverviewVO;
import com.libra.admin.vo.HotBookVO;
import com.libra.admin.vo.OverdueRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private LibBookMapper bookMapper;

    @Autowired
    private LibBorrowRecordMapper borrowRecordMapper;

    public DashboardOverviewVO getOverview() {
        DashboardOverviewVO vo = new DashboardOverviewVO();
        long totalBookTypes = bookMapper.selectCount(new LambdaQueryWrapper<LibBook>().eq(LibBook::getIsDeleted, 0));
        long totalInventory = bookMapper.selectSumTotalCount();
        long borrowingCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<LibBorrowRecord>()
                .eq(LibBorrowRecord::getStatus, 1));
        long overdueCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<LibBorrowRecord>()
                .eq(LibBorrowRecord::getStatus, 3));
        vo.setTotalBookTypes(totalBookTypes);
        vo.setTotalInventory(totalInventory);
        vo.setBorrowingCount(borrowingCount);
        vo.setOverdueCount(overdueCount);
        vo.setTodayBorrowCount(borrowRecordMapper.countBorrowToday());
        vo.setTodayReturnCount(borrowRecordMapper.countReturnToday());
        return vo;
    }

    public List<BorrowTrendVO> getBorrowTrend(int days) {
        int safeDays = days <= 0 ? 7 : days;
        return borrowRecordMapper.selectBorrowTrend(safeDays);
    }

    public List<HotBookVO> getHotBooks(int limit) {
        int safeLimit = limit <= 0 ? 5 : Math.min(limit, 50);
        return borrowRecordMapper.selectHotBooks(safeLimit);
    }

    public List<OverdueRecordVO> getOverdueRecords(int limit) {
        int safeLimit = limit <= 0 ? 5 : Math.min(limit, 50);
        return borrowRecordMapper.selectOverdueRecords(safeLimit);
    }
}
