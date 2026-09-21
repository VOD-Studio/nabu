package com.xfy.nabu.search.service;

import com.xfy.nabu.api.search.dto.TopicSearchDocDTO;
import com.xfy.nabu.common.result.PageResult;
import com.xfy.nabu.search.domain.TopicIndexDocument;

/**
 * 内部业务服务接口（区别于对外的 Dubbo {@code SearchService}），供本模块内部
 * （Dubbo 实现类 / RocketMQ 消费者）复用。
 */
public interface SearchBizService {

    PageResult<TopicSearchDocDTO> searchTopics(String keyword, long pageNum, long pageSize);

    /** upsert 单个帖子索引文档，供 CanalBinlogSyncEvent 消费者在 INSERT/UPDATE 时调用 */
    void upsert(TopicIndexDocument document);

    /** 删除单个帖子索引文档，供 CanalBinlogSyncEvent 消费者在 DELETE 时调用 */
    void delete(String topicId);
}
