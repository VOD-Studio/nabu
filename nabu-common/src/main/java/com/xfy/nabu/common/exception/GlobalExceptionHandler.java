package com.xfy.nabu.common.exception;

import com.xfy.nabu.common.result.Result;
import com.xfy.nabu.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。各 *-service / nabu-web 只要引入 nabu-common 即自动生效
 * （由 spring-boot-autoconfigure 的组件扫描或显式 @Import 注册，见各服务的启动类）。
 */
@RestControllerAdvice
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
