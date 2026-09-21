package com.xfy.nabu.forum.domain;

import com.xfy.nabu.common.base.BaseEntity;

/**
 * 评论实体，对应表 t_comment。
 */
public class CommentEntity extends BaseEntity {

    private Long topicId;
    private Long authorId;
    /** 被回复的评论 id，楼层评论为 null */
    private Long replyToId;
    private String content;

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
