package com.xfy.nabu.stat.service;

import com.xfy.nabu.api.stat.dto.HotTopicDTO;
import java.util.List;

/**
 * 内部业务服务接口（区别于对外的 Dubbo {@code StatService}），供本模块内部（Dubbo 实现类 / 未来的 REST 管理接口）复用。
 */
public interface StatBizService {

    void recordTopicView(Long topicId, Long viewerUserId);

    long getTopicViewCount(Long topicId);

    long getTopicUniqueVisitorCount(Long topicId);

    List<HotTopicDTO> topHotTopics(int limit);
}
