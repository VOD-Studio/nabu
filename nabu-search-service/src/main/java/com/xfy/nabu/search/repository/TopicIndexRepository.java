package com.xfy.nabu.search.repository;

import com.xfy.nabu.search.domain.TopicIndexDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * "nabu_topic_index" 索引的 Spring Data Elasticsearch Repository，
 * 用于 upsert（save）/ delete 单个文档；复杂的 multi_match 查询由
 * {@link com.xfy.nabu.search.service.impl.SearchBizServiceImpl} 直接使用 {@code ElasticsearchOperations} 完成。
 */
@Repository
public interface TopicIndexRepository extends ElasticsearchRepository<TopicIndexDocument, String> {}
