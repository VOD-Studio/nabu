package com.xfy.nabu.moderation.mq.listener;

import com.xfy.nabu.moderation.mq.event.CommentCreatedMessage;
import com.xfy.nabu.moderation.service.ModerationBizService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 消费 "CommentCreatedEvent"（生产方：nabu-forum-service）做异步复核。
 * 评论正文的同步预检已经在发帖/评论时通过 Dubbo 调用 {@code checkText} 拦截过一次，
 * 这里的异步复核是"双保险"：一方面覆盖同步预检之后敏感词库有更新的情况，
 * 另一方面落审核日志（t_moderation_log）供人工巡检追溯。
 */
@Component
@RocketMQMessageListener(topic = "CommentCreatedEvent", consumerGroup = "CommentCreatedEvent_MODERATION_CG")
public class CommentCreatedEventListener implements RocketMQListener<CommentCreatedMessage> {

    private final ModerationBizService moderationBizService;

    public CommentCreatedEventListener(ModerationBizService moderationBizService) {
        this.moderationBizService = moderationBizService;
    }

    @Override
    public void onMessage(CommentCreatedMessage msg) {
        moderationBizService.asyncReview("COMMENT", msg.getCommentId(), msg.getContent());
    }
}
