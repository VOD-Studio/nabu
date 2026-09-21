package com.xfy.nabu.common.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.util.UUID;

/**
 * 基于 TTL（TransmittableThreadLocal）的请求上下文：traceId / userId。
 * 在网关/拦截器入口写入，业务代码、异步线程池、Dubbo 调用链路中均可读取，
 * 用于日志打点与全链路追踪串联（配合 OpenTelemetry 一起使用）。
 */
public final class TraceContext {

    private static final TransmittableThreadLocal<String> TRACE_ID = new TransmittableThreadLocal<>();
    private static final TransmittableThreadLocal<Long> USER_ID = new TransmittableThreadLocal<>();

    private TraceContext() {}

    public static String getTraceId() {
        return TRACE_ID.get();
    }

    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId == null || traceId.isBlank() ? newTraceId() : traceId);
    }

    public static String newTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static void clear() {
        TRACE_ID.remove();
        USER_ID.remove();
    }
}
