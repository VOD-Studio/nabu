package com.xfy.nabu.common.exception;

import com.xfy.nabu.common.result.ResultCode;

/**
 * 业务异常：由业务代码主动抛出，携带错误码 + 提示信息，由全局异常处理器统一转换为 {@link com.xfy.nabu.common.result.Result}。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.SYSTEM_ERROR.getCode();
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
