package com.xfy.nabu.common.exception;

import com.xfy.nabu.common.result.Result;
import com.xfy.nabu.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。各可运行的 web 应用（nabu-web / nabu-admin-service / 暴露 Actuator 的各 *-service）
 * 通过组件扫描 com.xfy.nabu.common 自动注册；
 * {@code @ConditionalOnWebApplication(SERVLET)} 保证它只在 servlet web 上下文中生效，
 * 纯 Dubbo / 非 web 场景下即使扫到本类也不会实例化（本模块的 spring-web 依赖是 optional，不会传染）。
 */
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException ex) {
        log.warn("业务异常: code={}, message={}", ex.getCode(), ex.getMessage());
        return Result.failure(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleValidException(Exception ex) {
        log.warn("参数校验异常: {}", ex.getMessage());
        return Result.failure(ResultCode.PARAM_INVALID.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        log.error("系统异常", ex);
        return Result.failure(ResultCode.SYSTEM_ERROR);
    }
}
