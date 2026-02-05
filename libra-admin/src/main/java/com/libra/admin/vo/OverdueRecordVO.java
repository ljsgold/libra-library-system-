package com.libra.admin.vo;

import lombok.Data;

@Data
public class OverdueRecordVO {
    private Long recordId;
    private String userName;
    private String bookTitle;
    private Integer overdueDays;
}
