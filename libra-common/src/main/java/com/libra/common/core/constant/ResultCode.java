package com.libra.common.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统内部错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    BAD_REQUEST(400, "参数错误"),
    
    // 业务错误
    USER_NOT_EXISTS(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    TOKEN_EXPIRED(1003, "Token已过期"),
    BOOK_NOT_EXISTS(2001, "图书不存在"),
    INSUFFICIENT_STOCK(2002, "库存不足"),
    BORROW_LIMIT_REACHED(2003, "达到最大借阅量"),
    HAS_OVERDUE_BOOKS(2004, "存在超期未还图书");

    private final int code;
    private final String msg;
}
