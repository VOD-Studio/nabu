package com.xfy.nabu.notify.mq.event;

import java.io.Serializable;

/**
 * "CommentCreatedEvent" 消息体：由 nabu-forum-service 在评论创建成功后发布，
 * notify-service 消费该事件生成 COMMENT_REPLY 类型通知。
 */
public class CommentCreatedEvent implements Serializable {

    private Long commentId;
    private Long topicId;
    private Long authorId;
    /** 被回复的评论 id，楼层评论为 null（即直接评论帖子） */
    private Long replyToId;

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
}
