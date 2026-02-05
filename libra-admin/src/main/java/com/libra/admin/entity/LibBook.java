package com.libra.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("lib_book")
public class LibBook {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long tenantId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate pubDate;
    private BigDecimal price;
    private Long categoryId;
    private String coverUrl;
    private String summary;
    private Integer totalCount;
    private Integer stockCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer isDeleted;
}
