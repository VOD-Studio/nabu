package com.xfy.nabu.common.result;

import java.io.Serializable;

/**
 * 统一 API 返回体，所有 nabu-web / *-service 对外 HTTP 接口都返回该结构。
 *
 * @param <T> 业务数据类型
 */
public class Result<T> implements Serializable {

    private int code;
    private String message;
    private T data;
    private long timestamp = System.currentTimeMillis();
    /** 全链路追踪 id，便于日志/Trace 关联，来自 {@link com.xfy.nabu.common.util.TraceContext} */
    private String traceId;

    public Result() {}

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = ResultCode.SUCCESS.getCode();
        result.message = ResultCode.SUCCESS.getMessage();
        result.data = data;
        result.traceId = com.xfy.nabu.common.util.TraceContext.getTraceId();
        return result;
    }

    public static <T> Result<T> failure(ResultCode resultCode) {
        return failure(resultCode.getCode(), resultCode.getMessage());
    }

    public static <T> Result<T> failure(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        result.traceId = com.xfy.nabu.common.util.TraceContext.getTraceId();
        return result;
    }

    public boolean isSuccess() {
        return code == ResultCode.SUCCESS.getCode();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
