package com.libra.common.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_log")
public class SysLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long tenantId;
    private Long userId;
    private String username;
    private String module;
    private String operation;
    private String method;
    private String params;
    private String result;
    private Integer status;
    private String errorMsg;
    private String ip;
    private Long executionTime;
    private LocalDateTime createTime;
}
