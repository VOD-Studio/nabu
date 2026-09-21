package com.xfy.nabu.stat.service.impl;

import com.xfy.nabu.api.stat.dto.HotTopicDTO;
import com.xfy.nabu.stat.constant.StatRedisKeys;
import com.xfy.nabu.stat.service.StatBizService;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

/**
 * 话题 PV/UV/热门排行统计业务实现。全部基于 Redis 原生数据结构，不落 MySQL：
 * <ul>
 *     <li>PV：String + INCR，简单精确计数</li>
 *     <li>UV：HyperLogLog + PFADD/PFCOUNT，用极小空间（12KB 左右）换取近似（标准误差约 0.81%）去重计数，
 *     数据量越大越体现出相对于 Set 的空间优势</li>
 *     <li>热门排行：ZSet + ZINCRBY，天然按 score 排序，取 TopN 是 O(logN + M)</li>
 * </ul>
 */
@Service
public class StatBizServiceImpl implements StatBizService {

    /** 热度分增量：每次浏览简单加 1。生产环境常见做法是引入时间衰减因子（比如按小时/天做指数衰减），本项目简化为固定增量。 */
    private static final double HOT_SCORE_INCREMENT = 1.0d;

    private final StringRedisTemplate redisTemplate;

    public StatBizServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void recordTopicView(Long topicId, Long viewerUserId) {
        redisTemplate.opsForValue().increment(StatRedisKeys.pvKey(topicId));

        // viewerUserId 为 null 表示匿名访问（未登录用户），不参与 UV 去重统计，直接跳过 PFADD。
        if (viewerUserId != null) {
            redisTemplate.opsForHyperLogLog().add(StatRedisKeys.uvKey(topicId), String.valueOf(viewerUserId));
        }

        redisTemplate
                .opsForZSet()
                .incrementScore(StatRedisKeys.HOT_TOPICS_KEY, String.valueOf(topicId), HOT_SCORE_INCREMENT);
    }

    @Override
    public long getTopicViewCount(Long topicId) {
        String value = redisTemplate.opsForValue().get(StatRedisKeys.pvKey(topicId));
        return value == null ? 0L : Long.parseLong(value);
    }

    @Override
    public long getTopicUniqueVisitorCount(Long topicId) {
        Long size = redisTemplate.opsForHyperLogLog().size(StatRedisKeys.uvKey(topicId));
        return size == null ? 0L : size;
    }

    @Override
    public List<HotTopicDTO> topHotTopics(int limit) {
        int end = Math.max(limit - 1, 0);
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(StatRedisKeys.HOT_TOPICS_KEY, 0, end);
        if (tuples == null || tuples.isEmpty()) {
            return Collections.emptyList();
        }
        return tuples.stream()
                .map(tuple -> new HotTopicDTO(
                        Long.valueOf(tuple.getValue()), tuple.getScore() == null ? 0d : tuple.getScore()))
                .collect(Collectors.toList());
    }
}
