package com.libra.admin.vo;

import lombok.Data;

@Data
public class BookDetailVO extends BookItemVO {
    private String description;
    private String publisher;
    private String publishDate;
    private String locationInfo;
}
