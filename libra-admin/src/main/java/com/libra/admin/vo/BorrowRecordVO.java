package com.libra.admin.vo;

import lombok.Data;

@Data
public class BorrowRecordVO {
    private Long id;
    private String bookTitle;
    private String author;
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private String status;
    private boolean renewable;
}
