package com.xfy.nabu.moderation.mq.event;

import java.io.Serializable;

/**
 * "TopicCreatedEvent" 消息体（本服务侧的独立副本，字段与生产方 nabu-forum-service 保持一致）。
 */
public class TopicCreatedMessage implements Serializable {

    private Long topicId;
    private Long boardId;
    private Long authorId;
    private String title;
    private String content;

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
