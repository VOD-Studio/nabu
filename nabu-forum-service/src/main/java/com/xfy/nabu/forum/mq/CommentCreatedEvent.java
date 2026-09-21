package com.xfy.nabu.forum.mq;

import java.io.Serializable;

/**
 * "评论创建" 事件消息体。发布评论成功后由 {@code ForumBizServiceImpl#createComment} 通过
 * RocketMQTemplate 发送到 topic 名为 {@link #TOPIC} 的主题，供 nabu-moderation-service
 * （内容审核异步复核）、nabu-notify-service（@回复通知）等消费。
 */
public class CommentCreatedEvent implements Serializable {

    /** RocketMQ topic 名：固定字符串，跨服务约定一致 */
    public static final String TOPIC = "CommentCreatedEvent";

    private Long commentId;
    private Long topicId;
    private Long authorId;
    private Long replyToId;
    /** 评论正文：供审核等下游消费者直接取用 */
    private String content;

    public CommentCreatedEvent() {}

    public CommentCreatedEvent(Long commentId, Long topicId, Long authorId, Long replyToId, String content) {
        this.commentId = commentId;
        this.topicId = topicId;
        this.authorId = authorId;
        this.replyToId = replyToId;
        this.content = content;
    }

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
