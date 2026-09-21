package com.xfy.nabu.common.result;

/**
 * 统一返回码。业务码约定：
 * - 0      成功
 * - 1000+  通用错误
 * - 2000+  auth-service
 * - 3000+  user-service
 * - 4000+  forum-service
 * - 5000+  social-service
 * - 6000+  file-service
 * - 7000+  其余服务按需扩展
 */
public enum ResultCode {
    SUCCESS(0, "success"),

    PARAM_INVALID(1000, "参数不合法"),
    UNAUTHORIZED(1001, "未登录或登录已过期"),
    FORBIDDEN(1002, "无权限访问该资源"),
    NOT_FOUND(1004, "资源不存在"),
    RATE_LIMITED(1029, "请求过于频繁，请稍后再试"),
    SYSTEM_ERROR(1500, "系统异常，请稍后再试");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
