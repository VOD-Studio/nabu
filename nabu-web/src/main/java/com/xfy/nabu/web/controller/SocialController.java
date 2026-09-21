package com.xfy.nabu.web.controller;

import com.xfy.nabu.api.social.dto.LikeEventDTO;
import com.xfy.nabu.api.social.service.SocialService;
import com.xfy.nabu.common.result.Result;
import com.xfy.nabu.common.util.TraceContext;
import com.xfy.nabu.web.dto.ToggleLikeRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 点赞 / 关注 / 签到接口。均为薄转发层，调用 {@link SocialService} 完成实际业务逻辑，
 * 用户 id 取自 {@link TraceContext#getUserId()}（由 JWT 拦截器写入）。
 */
@RestController
public class SocialController {

    @DubboReference
    private SocialService socialService;

    /**
     * 点赞 / 取消点赞。
     */
    @PostMapping("/api/v1/social/likes")
    public Result<Void> toggleLike(@RequestBody ToggleLikeRequest request) {
        LikeEventDTO event = new LikeEventDTO();
        event.setTargetType(request.getTargetType());
        event.setTargetId(request.getTargetId());
        event.setLiked(request.isLiked());
        event.setUserId(TraceContext.getUserId());
        socialService.toggleLike(event);
        return Result.success();
    }

    /**
     * 关注某个用户。
     */
    @PostMapping("/api/v1/social/follow/{followeeId}")
    public Result<Void> follow(@PathVariable("followeeId") Long followeeId) {
        socialService.follow(TraceContext.getUserId(), followeeId);
        return Result.success();
    }

    /**
     * 取消关注某个用户。
     */
    @DeleteMapping("/api/v1/social/follow/{followeeId}")
    public Result<Void> unfollow(@PathVariable("followeeId") Long followeeId) {
        socialService.unfollow(TraceContext.getUserId(), followeeId);
        return Result.success();
    }

    /**
     * 每日签到，返回连续签到天数。
     */
    @PostMapping("/api/v1/social/checkin")
    public Result<Integer> checkin() {
        int consecutiveDays = socialService.checkin(TraceContext.getUserId());
        return Result.success(consecutiveDays);
    }
}
