package com.xfy.nabu.forum.mq;

import java.io.Serializable;

/**
 * "帖子创建" 事件消息体。发帖成功后由 {@code ForumBizServiceImpl#createTopic} 通过
 * RocketMQTemplate 发送到 topic 名为 {@link #TOPIC} 的主题，供 nabu-moderation-service
 * （内容审核异步复核）、nabu-search-service（建索引）、nabu-stat-service（统计）等消费。
 */
public class TopicCreatedEvent implements Serializable {

    /** RocketMQ topic 名：固定字符串，跨服务约定一致 */
    public static final String TOPIC = "TopicCreatedEvent";

    private Long topicId;
    private Long boardId;
    private Long authorId;
    private String title;
    /** 帖子正文：供审核/搜索等下游消费者直接取用，避免二次回查 forum-service */
    private String content;

    public TopicCreatedEvent() {
    }

    public TopicCreatedEvent(Long topicId, Long boardId, Long authorId, String title, String content) {
        this.topicId = topicId;
        this.boardId = boardId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
    }

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
