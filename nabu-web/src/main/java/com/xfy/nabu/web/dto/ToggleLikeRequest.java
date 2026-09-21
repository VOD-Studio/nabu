package com.xfy.nabu.web.dto;

/**
 * 点赞/取消点赞请求体。用户 id 不由前端传入，而是从 {@code TraceContext.getUserId()} 取当前登录用户。
 */
public class ToggleLikeRequest {

    /** 目标类型：TOPIC / COMMENT */
    private String targetType;
    private Long targetId;
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

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
