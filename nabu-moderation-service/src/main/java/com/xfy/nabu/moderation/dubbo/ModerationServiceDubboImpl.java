package com.xfy.nabu.moderation.dubbo;

import com.xfy.nabu.api.moderation.dto.ModerationResultDTO;
import com.xfy.nabu.api.moderation.service.ModerationService;
import com.xfy.nabu.moderation.service.ModerationBizService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * Dubbo Provider：对外暴露 {@link ModerationService}，供 forum-service 发帖/评论前同步预检。
 */
@DubboService
public class ModerationServiceDubboImpl implements ModerationService {

    private final ModerationBizService moderationBizService;

    public ModerationServiceDubboImpl(ModerationBizService moderationBizService) {
        this.moderationBizService = moderationBizService;
    }

    @Override
    public ModerationResultDTO checkText(String content) {
        return moderationBizService.checkText(content);
    }
}
