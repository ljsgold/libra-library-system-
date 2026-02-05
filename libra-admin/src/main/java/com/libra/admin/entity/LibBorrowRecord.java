package com.libra.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("lib_borrow_record")
public class LibBorrowRecord {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long tenantId;
    private Long inventoryId;
    private Long bookId;
    private Long userId;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private Integer status; // 1:借阅中, 2:已归还, 3:已逾期, 4:已赔付
    private BigDecimal fineAmount;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
