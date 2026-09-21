package com.xfy.nabu.moderation.mq.listener;

import com.xfy.nabu.moderation.mq.event.TopicCreatedMessage;
import com.xfy.nabu.moderation.service.ModerationBizService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 消费 "TopicCreatedEvent"（生产方：nabu-forum-service）做异步复核。
 * 帖子标题和正文都参与检测：标题命中敏感词的概率不低，且标题往往是引流/广告话术的重灾区。
 */
@Component
@RocketMQMessageListener(
        topic = "TopicCreatedEvent",
        consumerGroup = "TopicCreatedEvent_MODERATION_CG"
)
public class TopicCreatedEventListener implements RocketMQListener<TopicCreatedMessage> {

    private final ModerationBizService moderationBizService;

    public TopicCreatedEventListener(ModerationBizService moderationBizService) {
        this.moderationBizService = moderationBizService;
    }

    @Override
    public void onMessage(TopicCreatedMessage msg) {
        String textToReview = (msg.getTitle() == null ? "" : msg.getTitle()) + "\n" + (msg.getContent() == null ? "" : msg.getContent());
        moderationBizService.asyncReview("TOPIC", msg.getTopicId(), textToReview);
    }
}
