package com.xfy.nabu.social.service;

import com.xfy.nabu.api.social.dto.LikeEventDTO;

/**
 * 内部业务服务接口（区别于对外的 Dubbo {@code SocialService}），供本模块内部（Dubbo 实现类 / 未来的 REST 管理接口）复用。
 */
public interface SocialBizService {

    void toggleLike(LikeEventDTO event);

    boolean hasLiked(String targetType, Long targetId, Long userId);

    long countLikes(String targetType, Long targetId);

    void follow(Long followerId, Long followeeId);

    void unfollow(Long followerId, Long followeeId);

    /** 每日签到，返回连续签到天数（简化实现，见 {@link com.xfy.nabu.social.service.impl.SocialBizServiceImpl}） */
    int checkin(Long userId);
}
