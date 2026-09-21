package com.xfy.nabu.social.service.impl;

import com.xfy.nabu.api.social.dto.LikeEventDTO;
import com.xfy.nabu.common.exception.BusinessException;
import com.xfy.nabu.common.result.ResultCode;
import com.xfy.nabu.social.constant.SocialRedisKeys;
import com.xfy.nabu.social.mq.event.TopicLikedEvent;
import com.xfy.nabu.social.mq.event.UserFollowedEvent;
import com.xfy.nabu.social.service.SocialBizService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 点赞/收藏/关注/签到业务实现。全部基于 Redis 原生数据结构，不落 MySQL：
 * <ul>
 *     <li>点赞：Set（key 维度是「目标」，member 是 userId），天然去重、O(1) 判重与计数</li>
 *     <li>关注：双向 Set（正向 following + 反向 followers），互相独立更新以支撑「我关注了谁」「谁关注了我」两类查询</li>
 *     <li>签到：Bitmap（一个月一个 key，一天一个 bit），空间极省，且天然支持位运算统计</li>
 * </ul>
 */
@Service
public class SocialBizServiceImpl implements SocialBizService {

    private static final Logger log = LoggerFactory.getLogger(SocialBizServiceImpl.class);

    /** 仅话题维度的点赞才需要联动发布 MQ 事件，评论点赞暂不需要 */
    private static final String TARGET_TYPE_TOPIC = "TOPIC";

    /** MQ topic 名固定为类名字符串，方便下游按同名 Consumer Group 订阅 */
    private static final String MQ_TOPIC_LIKED = "TopicLikedEvent";
    private static final String MQ_TOPIC_FOLLOWED = "UserFollowedEvent";

    private final StringRedisTemplate redisTemplate;
    private final RocketMQTemplate rocketMQTemplate;

    public SocialBizServiceImpl(StringRedisTemplate redisTemplate, RocketMQTemplate rocketMQTemplate) {
        this.redisTemplate = redisTemplate;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    public void toggleLike(LikeEventDTO event) {
        String key = SocialRedisKeys.likeSetKey(event.getTargetType(), event.getTargetId());
        String member = String.valueOf(event.getUserId());
        if (event.isLiked()) {
            redisTemplate.opsForSet().add(key, member);
        } else {
            redisTemplate.opsForSet().remove(key, member);
        }

        // 只有话题被点赞/取消点赞时才广播事件，供 forum-service 更新热度、stat-service 更新排行榜等下游消费。
        if (TARGET_TYPE_TOPIC.equals(event.getTargetType())) {
            TopicLikedEvent mqEvent = new TopicLikedEvent(event.getTargetId(), event.getUserId(), event.isLiked());
            rocketMQTemplate.convertAndSend(MQ_TOPIC_LIKED, mqEvent);
            log.info("发布 TopicLikedEvent: targetId={}, userId={}, liked={}",
                    event.getTargetId(), event.getUserId(), event.isLiked());
        }
    }

    @Override
    public boolean hasLiked(String targetType, Long targetId, Long userId) {
        String key = SocialRedisKeys.likeSetKey(targetType, targetId);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, String.valueOf(userId));
        return Boolean.TRUE.equals(isMember);
    }

    @Override
    public long countLikes(String targetType, Long targetId) {
        String key = SocialRedisKeys.likeSetKey(targetType, targetId);
        Long size = redisTemplate.opsForSet().size(key);
        return size == null ? 0L : size;
    }

    @Override
    public void follow(Long followerId, Long followeeId) {
        if (followerId == null || followeeId == null || followerId.equals(followeeId)) {
            throw new BusinessException(ResultCode.PARAM_INVALID.getCode(), "参数不合法或不能关注自己");
        }
        redisTemplate.opsForSet().add(SocialRedisKeys.followingSetKey(followerId), String.valueOf(followeeId));
        redisTemplate.opsForSet().add(SocialRedisKeys.followersSetKey(followeeId), String.valueOf(followerId));

        rocketMQTemplate.convertAndSend(MQ_TOPIC_FOLLOWED, new UserFollowedEvent(followerId, followeeId));
        log.info("发布 UserFollowedEvent: followerId={}, followeeId={}", followerId, followeeId);
    }

    @Override
    public void unfollow(Long followerId, Long followeeId) {
        redisTemplate.opsForSet().remove(SocialRedisKeys.followingSetKey(followerId), String.valueOf(followeeId));
        redisTemplate.opsForSet().remove(SocialRedisKeys.followersSetKey(followeeId), String.valueOf(followerId));
    }

    @Override
    public int checkin(Long userId) {
        LocalDate today = LocalDate.now();
        String yyyyMM = today.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String key = SocialRedisKeys.checkinBitmapKey(userId, yyyyMM);

        // bit offset 采用「当月第几天，从 0 开始」，即 1 号对应 offset 0，2 号对应 offset 1……
        int todayOffset = today.getDayOfMonth() - 1;
        redisTemplate.opsForValue().setBit(key, todayOffset, true);

        // 简化实现：只统计「当月内」的连续签到天数——从今天的 bit 往前逐位查，遇到第一个未签到（bit=0）的日子就停止累加。
        // 若连续签到跨越了月初（例如上月最后几天也连续签到），本实现不会拼接上月的 bitmap，属于有意简化，
        // 生产环境可以按自然周期滚动 key 或用一个不分月的长期 bitmap 来避免跨月断点。
        int streak = 0;
        for (int offset = todayOffset; offset >= 0; offset--) {
            Boolean bit = redisTemplate.opsForValue().getBit(key, offset);
            if (Boolean.TRUE.equals(bit)) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }
}
