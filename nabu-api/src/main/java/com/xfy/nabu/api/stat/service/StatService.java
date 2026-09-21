package com.xfy.nabu.api.stat.service;

import com.xfy.nabu.api.stat.dto.HotTopicDTO;
import java.util.List;

/**
 * 统计服务 Dubbo 接口，由 nabu-stat-service 提供实现。
 * PV 用 Redis String 累加，UV 用 HyperLogLog 近似统计，热门排行用 ZSet。
 */
public interface StatService {

    void recordTopicView(Long topicId, Long viewerUserId);

    long getTopicViewCount(Long topicId);

    long getTopicUniqueVisitorCount(Long topicId);

    List<HotTopicDTO> topHotTopics(int limit);
}
