package com.libra.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookAddDTO {
    @NotBlank(message = "ISBN不能为空")
    private String isbn;

    @NotBlank(message = "书名不能为空")
    private String title;

    private String author;
    private String publisher;
    private LocalDate pubDate;
    private BigDecimal price;
    private Long categoryId;
    private String coverUrl;
    private String summary;

    @NotNull(message = "副本数量不能为空")
    @Min(value = 1, message = "副本数量至少为1")
    private Integer copyCount;
}
