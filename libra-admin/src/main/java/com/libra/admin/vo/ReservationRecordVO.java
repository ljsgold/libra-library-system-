package com.libra.admin.vo;

import lombok.Data;

@Data
public class ReservationRecordVO {
    private Long id;
    private String bookTitle;
    private String author;
    private String status;
    private Integer queueNo;
    private String expectedDate;
    private String pickupDeadline;
    private Boolean notified;
    private String createdAt;
}
