package com.dylan.farmhouse.common.result;

import lombok.Getter;

/**
 * 返回码枚举。
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "成功"),
    FAIL(500, "系统繁忙，请稍后重试"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // 用户模块 1xxx
    USERNAME_EXIST(1001, "用户名已存在"),
    USERNAME_OR_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_NOT_FOUND(1003, "用户不存在"),

    // 产品模块 2xxx
    PRODUCT_NOT_FOUND(2001, "服务产品不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
