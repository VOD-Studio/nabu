package com.xfy.nabu.api.search.dto;

import java.io.Serializable;

/**
 * 帖子搜索索引文档，由 search-service 消费 Canal -> RocketMQ 的 binlog 事件后写入 Elasticsearch。
 */
public class TopicSearchDocDTO implements Serializable {

    private Long id;
    private Long boardId;
    private Long authorId;
    private String title;
    private String content;
    private long createdAtEpochMillis;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public long getCreatedAtEpochMillis() {
        return createdAtEpochMillis;
    }

    public void setCreatedAtEpochMillis(long createdAtEpochMillis) {
        this.createdAtEpochMillis = createdAtEpochMillis;
    }
}
