package com.xfy.nabu.notify.mq.event;

import java.io.Serializable;

/**
 * "TopicLikedEvent" 消息体：由 nabu-social-service 在用户点赞帖子后发布，
 * notify-service 消费该事件生成 LIKE 类型通知，接收人是被点赞帖子的作者。
 */
public class TopicLikedEvent implements Serializable {

    /** 目标类型：TOPIC / COMMENT，本服务目前只处理 TOPIC */
    private String targetType;
    /** 被点赞的目标 id（targetType=TOPIC 时为 topicId） */
    private Long targetId;
    /** 点赞发起人 id */
    private Long userId;

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
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
}
