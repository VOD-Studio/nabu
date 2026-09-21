package com.xfy.nabu.notify.mq.listener;

import com.xfy.nabu.notify.mq.event.UserFollowedEvent;
import com.xfy.nabu.notify.service.NotifyBizService;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 消费 "UserFollowedEvent"：nabu-social-service 在用户关注成功后发布该事件，
 * 本监听器据此生成通知，receiver 是被关注者（followeeId）。
 *
 * <p>{@link com.xfy.nabu.api.notify.dto.NotificationDTO#getType()} 是字符串而非枚举，
 * 这里直接使用语义更明确的 "FOLLOW" 作为 type 值（而不是复用 MENTION），
 * 便于前端根据 type 渲染不同的通知图标/文案。</p>
 *
 * <p>消费幂等说明：同 {@link CommentCreatedEventListener}，生产环境应引入消息去重表或
 * 唯一业务键防止 MQ 重试导致重复通知。</p>
 */
@Component
@RocketMQMessageListener(
        topic = "UserFollowedEvent",
        consumerGroup = "UserFollowedEvent_NOTIFY_CG",
        consumeMode = ConsumeMode.CONCURRENTLY)
public class UserFollowedEventListener implements RocketMQListener<UserFollowedEvent> {

    private static final Logger log = LoggerFactory.getLogger(UserFollowedEventListener.class);

    private final NotifyBizService notifyBizService;

    public UserFollowedEventListener(NotifyBizService notifyBizService) {
        this.notifyBizService = notifyBizService;
    }

    @Override
    public void onMessage(UserFollowedEvent event) {
        if (event.getFolloweeId() == null || event.getFollowerId() == null) {
            log.warn("UserFollowedEvent 缺少必要字段，忽略：{}", event);
            return;
        }
        try {
            String content = "用户 " + event.getFollowerId() + " 关注了你";
            String linkUrl = "/user/" + event.getFollowerId();
            notifyBizService.createNotification(event.getFolloweeId(), "FOLLOW", content, linkUrl);
        } catch (Exception e) {
            log.error("处理 UserFollowedEvent 失败：{}", event, e);
            throw e;
        }
    }
}
