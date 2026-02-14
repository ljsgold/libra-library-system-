package com.libra.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lib_category")
public class LibCategory {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long tenantId;
    private String categoryCode;
    private String categoryName;
    private Long parentId;
    private Integer sortOrder;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer isDeleted;
}
