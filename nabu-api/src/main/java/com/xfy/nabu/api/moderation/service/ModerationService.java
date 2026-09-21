package com.xfy.nabu.api.moderation.service;

import com.xfy.nabu.api.moderation.dto.ModerationResultDTO;

/**
 * 内容审核服务 Dubbo 接口，由 nabu-moderation-service 提供实现。
 * 主要通过消费 RocketMQ 的 TopicCreatedEvent / CommentCreatedEvent 异步审核，
 * 该接口暴露给 forum-service 做同步预检（例如敏感词快速拦截）。
 */
public interface ModerationService {

    ModerationResultDTO checkText(String content);
}
