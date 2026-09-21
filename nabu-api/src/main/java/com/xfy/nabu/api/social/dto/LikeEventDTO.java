package com.xfy.nabu.api.social.dto;

import java.io.Serializable;

public class LikeEventDTO implements Serializable {

    /** 目标类型：TOPIC / COMMENT */
    private String targetType;
    private Long targetId;
    private Long userId;
    private boolean liked;

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

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
