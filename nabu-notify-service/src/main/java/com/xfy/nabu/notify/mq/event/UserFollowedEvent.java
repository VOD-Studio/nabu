package com.xfy.nabu.notify.mq.event;

import java.io.Serializable;

/**
 * "UserFollowedEvent" 消息体：由 nabu-social-service 在用户关注成功后发布，
 * notify-service 消费该事件生成 FOLLOW 类型通知，接收人是被关注者（followeeId）。
 */
public class UserFollowedEvent implements Serializable {

    private Long followerId;
    private Long followeeId;

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
