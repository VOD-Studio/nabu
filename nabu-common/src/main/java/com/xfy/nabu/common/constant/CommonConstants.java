package com.xfy.nabu.common.constant;

/**
 * 跨服务共用常量：HTTP 头、Dubbo 隐式参数 key、Redis key 前缀等。
 */
public final class CommonConstants {

    private CommonConstants() {
    }

    /** 网关/nabu-web 注入的当前登录用户 id，通过 HTTP Header 在内部调用间传递 */
    public static final String HEADER_USER_ID = "X-User-Id";

    /** 全链路追踪 id 的 HTTP Header / MDC key */
    public static final String HEADER_TRACE_ID = "X-Trace-Id";
    public static final String MDC_TRACE_ID = "traceId";

    /** Dubbo attachment key，用于跨进程透传 TTL 上下文 */
    public static final String DUBBO_ATTACHMENT_USER_ID = "userId";
    public static final String DUBBO_ATTACHMENT_TRACE_ID = "traceId";

    /** Redis key 前缀 */
    public static final String REDIS_KEY_PREFIX = "nabu:";
}
