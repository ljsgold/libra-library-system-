package com.libra.admin.vo;

import lombok.Data;

@Data
public class BookItemVO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String categoryName;
    private String coverUrl;
    private boolean canBorrow;
    private int availableCount;
    private int totalCount;
}
