package com.xfy.nabu.social.dubbo;

import com.xfy.nabu.api.social.dto.LikeEventDTO;
import com.xfy.nabu.api.social.service.SocialService;
import com.xfy.nabu.social.service.SocialBizService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * Dubbo Provider：对外暴露 {@link SocialService}，内部转发给 {@link SocialBizService}。
 * 这样拆分是为了让业务逻辑不直接依赖 Dubbo 注解，方便未来暴露 REST 管理接口时复用同一套业务代码。
 */
@DubboService
public class SocialServiceDubboImpl implements SocialService {

    private final SocialBizService socialBizService;

    public SocialServiceDubboImpl(SocialBizService socialBizService) {
        this.socialBizService = socialBizService;
    }

    @Override
    public void toggleLike(LikeEventDTO event) {
        socialBizService.toggleLike(event);
    }

    @Override
    public boolean hasLiked(String targetType, Long targetId, Long userId) {
        return socialBizService.hasLiked(targetType, targetId, userId);
    }

    @Override
    public long countLikes(String targetType, Long targetId) {
        return socialBizService.countLikes(targetType, targetId);
    }

    @Override
    public void follow(Long followerId, Long followeeId) {
        socialBizService.follow(followerId, followeeId);
    }

    @Override
    public void unfollow(Long followerId, Long followeeId) {
        socialBizService.unfollow(followerId, followeeId);
    }

    @Override
    public int checkin(Long userId) {
        return socialBizService.checkin(userId);
    }
}
