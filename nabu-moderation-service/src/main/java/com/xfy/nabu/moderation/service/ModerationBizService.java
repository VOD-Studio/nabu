package com.xfy.nabu.moderation.service;

import com.xfy.nabu.api.moderation.dto.ModerationResultDTO;

/**
 * 内部业务服务接口（区别于对外的 Dubbo {@code ModerationService}）。
 */
public interface ModerationBizService {

    /** 同步预检：给一段文本，返回 PASS/REJECT */
    ModerationResultDTO checkText(String content);

    /**
     * 异步复核：给一条已落库的帖子/评论内容做敏感词检测，并把审核结论落 t_moderation_log
     * （供人工巡检/追溯用），不改变 forum-service 侧帖子/评论本身的可见状态。
     */
    void asyncReview(String targetType, Long targetId, String content);
}
