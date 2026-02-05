package com.libra.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminBorrowRecordVO {
    private Long recordId;
    private String userName;
    private String bookTitle;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private Integer status;
    private Integer overdueDays;
}
