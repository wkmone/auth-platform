package com.company.auth.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // Auth
    INVALID_CREDENTIALS(1001, "用户名或密码错误"),
    ACCOUNT_LOCKED(1002, "账户已被锁定"),
    ACCOUNT_DISABLED(1003, "账户已被禁用"),
    PASSWORD_EXPIRED(1004, "密码已过期"),
    INVALID_TOKEN(1005, "令牌无效"),
    TOKEN_EXPIRED(1006, "令牌已过期"),
    INVALID_CLIENT(1007, "无效的客户端"),
    INVALID_REDIRECT_URI(1008, "回调地址不匹配"),

    // User
    USER_NOT_FOUND(2001, "用户不存在"),
    USERNAME_EXISTS(2002, "用户名已存在"),
    PASSWORD_REUSED(2003, "新密码与历史密码重复"),
    PASSWORD_TOO_WEAK(2004, "密码强度不足"),

    // App
    APP_NOT_FOUND(3001, "应用不存在"),
    APP_ALREADY_APPROVED(3002, "应用已审批"),
    SECRET_VIEW_LIMIT(3003, "Secret 仅可查看一次");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
