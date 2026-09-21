package com.xfy.nabu.notify.mq.listener;

import com.xfy.nabu.api.forum.dto.TopicDTO;
import com.xfy.nabu.api.forum.service.ForumService;
import com.xfy.nabu.notify.mq.event.TopicLikedEvent;
import com.xfy.nabu.notify.service.NotifyBizService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 消费 "TopicLikedEvent"：nabu-social-service 在用户点赞帖子后发布该事件，
 * 本监听器据此生成 type=LIKE 的通知，receiver 是被点赞帖子的作者
 * （通过 {@code @DubboReference} 调用 {@link ForumService#getTopicById} 查出 authorId）。
 *
 * <p>目前只处理 targetType=TOPIC 的点赞；对评论点赞（targetType=COMMENT）暂不生成通知，
 * 如需支持可参照 {@link CommentCreatedEventListener} 的说明扩展 ForumService 按 commentId 查作者。</p>
 *
 * <p>消费幂等说明：同 {@link CommentCreatedEventListener}，生产环境应引入消息去重表或
 * 唯一业务键防止 MQ 重试导致重复通知。</p>
 */
@Component
@RocketMQMessageListener(
        topic = "TopicLikedEvent",
        consumerGroup = "TopicLikedEvent_NOTIFY_CG",
        consumeMode = ConsumeMode.CONCURRENTLY)
public class TopicLikedEventListener implements RocketMQListener<TopicLikedEvent> {

    private static final Logger log = LoggerFactory.getLogger(TopicLikedEventListener.class);

    @DubboReference
    private ForumService forumService;

    private final NotifyBizService notifyBizService;

    public TopicLikedEventListener(NotifyBizService notifyBizService) {
        this.notifyBizService = notifyBizService;
    }

    @Override
    public void onMessage(TopicLikedEvent event) {
        if (!"TOPIC".equals(event.getTargetType())) {
            log.debug("忽略非 TOPIC 类型的点赞事件：targetType={}", event.getTargetType());
            return;
        }
        try {
            TopicDTO topic = forumService.getTopicById(event.getTargetId());
            if (topic == null) {
                log.warn("TopicLikedEvent 对应的帖子不存在，忽略：targetId={}", event.getTargetId());
                return;
            }
            Long receiverId = topic.getAuthorId();
            if (receiverId == null || receiverId.equals(event.getUserId())) {
                // 帖子作者给自己点赞，不需要通知
                return;
            }
            String content = "有人点赞了你的帖子《" + topic.getTitle() + "》";
            String linkUrl = "/topic/" + topic.getId();
            notifyBizService.createNotification(receiverId, "LIKE", content, linkUrl);
        } catch (Exception e) {
            log.error("处理 TopicLikedEvent 失败：{}", event, e);
            throw e;
        }
    }
}
