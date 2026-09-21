package com.xfy.nabu.search.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 帖子搜索索引文档，映射到 Elasticsearch 索引 "nabu_topic_index"。
 * 字段与 {@link com.xfy.nabu.api.search.dto.TopicSearchDocDTO} 保持一一对应，
 * 写入完全来自 CanalBinlogSyncEvent 消费逻辑（见 {@link com.xfy.nabu.search.mq.listener.CanalBinlogSyncEventListener}），
 * 不在其他业务代码里直接写入。
 *
 * <p>title / content 使用默认的 standard analyzer 分词（本项目本地环境未安装 IK 分词插件）。
 * 生产环境中文全文搜索建议为 ES 安装 IK 分词插件（analyzer/search_analyzer 指定为 "ik_max_word"/"ik_smart"），
 * 以获得更符合中文语义的分词效果。</p>
 */
@Document(indexName = "nabu_topic_index")
public class TopicIndexDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long boardId;

    @Field(type = FieldType.Long)
    private Long authorId;

    /** 标题：权重更高，参与 multi_match 查询。生产环境建议安装 IK 分词插件替换默认 standard analyzer。 */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    /** 正文内容。生产环境建议安装 IK 分词插件替换默认 standard analyzer。 */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    @Field(type = FieldType.Long)
    private long createdAtEpochMillis;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
