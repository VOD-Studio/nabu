package com.xfy.nabu.api.search.service;

import com.xfy.nabu.api.search.dto.TopicSearchDocDTO;
import com.xfy.nabu.common.result.PageResult;

/**
 * 搜索服务 Dubbo 接口，由 nabu-search-service 提供实现。
 * 索引写入完全走 Canal(binlog) -> RocketMQ -> search-service 消费，不在业务代码里同步双写 ES，
 * 该接口只暴露查询能力。
 */
public interface SearchService {

    PageResult<TopicSearchDocDTO> searchTopics(String keyword, long pageNum, long pageSize);
}
