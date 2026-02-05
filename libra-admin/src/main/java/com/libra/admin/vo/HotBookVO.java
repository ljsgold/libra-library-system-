package com.libra.admin.vo;

import lombok.Data;

@Data
public class HotBookVO {
    private Long bookId;
    private String title;
    private String author;
    private Long borrowCount;
}
