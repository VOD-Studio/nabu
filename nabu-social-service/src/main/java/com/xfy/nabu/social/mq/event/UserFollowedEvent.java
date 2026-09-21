package com.xfy.nabu.social.mq.event;

import java.io.Serializable;

/**
 * 用户关注事件，MQ topic 固定为 {@code "UserFollowedEvent"}。
 * follow 成功后发布，供 notify-service（给被关注者发通知）等下游消费。
 */
public class UserFollowedEvent implements Serializable {

    private Long followerId;
    private Long followeeId;

    public UserFollowedEvent() {}

    public UserFollowedEvent(Long followerId, Long followeeId) {
        this.followerId = followerId;
        this.followeeId = followeeId;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public Long getFolloweeId() {
        return followeeId;
    }

    public void setFolloweeId(Long followeeId) {
        this.followeeId = followeeId;
    }
}
