package com.xfy.nabu.social.mq.event;

import java.io.Serializable;

/**
 * 话题被点赞/取消点赞事件，MQ topic 固定为 {@code "TopicLikedEvent"}。
 * 仅当 {@code targetType == "TOPIC"} 时由 {@code SocialBizService} 发布，供 forum-service（更新话题热度）、
 * stat-service（排行榜）等下游消费。
 */
public class TopicLikedEvent implements Serializable {

    private Long targetId;
    private Long userId;
    private boolean liked;

    public TopicLikedEvent() {
    }

    public TopicLikedEvent(Long targetId, Long userId, boolean liked) {
        this.targetId = targetId;
        this.userId = userId;
        this.liked = liked;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
