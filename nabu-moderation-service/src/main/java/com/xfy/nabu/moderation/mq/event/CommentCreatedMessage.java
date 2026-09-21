package com.xfy.nabu.moderation.mq.event;

import java.io.Serializable;

/**
 * "CommentCreatedEvent" 消息体（本服务侧的独立副本，字段与生产方 nabu-forum-service 保持一致）。
 */
public class CommentCreatedMessage implements Serializable {

    private Long commentId;
    private Long topicId;
    private Long authorId;
    private Long replyToId;
    private String content;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

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
