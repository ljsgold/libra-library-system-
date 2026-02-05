package com.libra.admin.vo;

import lombok.Data;

@Data
public class DashboardOverviewVO {
    private long totalBookTypes;
    private long totalInventory;
    private long borrowingCount;
    private long overdueCount;
    private long todayBorrowCount;
    private long todayReturnCount;
}
