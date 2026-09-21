package com.xfy.nabu.api.user.dto;

import java.io.Serializable;

/**
 * 积分变更请求：由 forum-service（置顶）、social-service（签到/点赞奖励）等通过 Dubbo 调用，
 * user-service 内部使用 Seata @GlobalTransactional 保证与调用方的分布式事务一致性。
 */
public class PointsChangeDTO implements Serializable {

    private Long userId;
    /** 正数增加，负数扣减 */
    private Integer delta;
    private String reason;
    /** 幂等去重 key，例如 "topic:pin:{topicId}" */
    private String idempotentKey;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getIdempotentKey() {
        return idempotentKey;
    }

    public void setIdempotentKey(String idempotentKey) {
        this.idempotentKey = idempotentKey;
    }
}
