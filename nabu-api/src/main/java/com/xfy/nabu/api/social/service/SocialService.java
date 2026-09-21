package com.xfy.nabu.api.social.service;

import com.xfy.nabu.api.social.dto.LikeEventDTO;

/**
 * 点赞 / 收藏 / 关注 / 签到服务 Dubbo 接口，由 nabu-social-service 提供实现。
 * 内部使用 Redis：Set 存点赞用户、ZSet 存热门排行、Bitmap 存签到。
 */
public interface SocialService {

    void toggleLike(LikeEventDTO event);

    boolean hasLiked(String targetType, Long targetId, Long userId);

    long countLikes(String targetType, Long targetId);

    void follow(Long followerId, Long followeeId);

    void unfollow(Long followerId, Long followeeId);

    /** 每日签到，返回连续签到天数 */
    int checkin(Long userId);
}
