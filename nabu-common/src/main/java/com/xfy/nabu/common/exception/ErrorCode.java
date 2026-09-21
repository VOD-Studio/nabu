package com.xfy.nabu.common.exception;

/**
 * 业务异常错误码约定接口。各服务可实现枚举，落到该接口便于统一捕获处理。
 */
public interface ErrorCode {

    int getCode();

    String getMessage();
}
