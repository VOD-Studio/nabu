package com.xfy.nabu.web.dto;

/**
 * 创建评论请求体。作者 id 不由前端传入，而是从 {@code TraceContext.getUserId()} 取当前登录用户。
 */
public class CreateCommentRequest {

    /** 被回复的评论 id，楼层评论为 null */
    private Long replyToId;

    private String content;

    public Long getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(Long replyToId) {
        this.replyToId = replyToId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
