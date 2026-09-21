package com.xfy.nabu.notify.mq.listener;

import com.xfy.nabu.api.forum.dto.TopicDTO;
import com.xfy.nabu.api.forum.service.ForumService;
import com.xfy.nabu.notify.mq.event.CommentCreatedEvent;
import com.xfy.nabu.notify.service.NotifyBizService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 消费 "CommentCreatedEvent"：nabu-forum-service 在评论创建成功后发布该事件，
 * 本监听器据此生成 type=COMMENT_REPLY 的通知。
 *
 * <p>接收人定位说明：理想情况下，如果 {@code replyToId} 不为空（说明这是回复某条楼层评论），
 * 通知的接收人应该是"被回复的那条评论"的作者；但 notify-service 没有 nabu-forum 库的直接数据库访问权限，
 * 因此这里简化为：一律把"帖子作者"当作 receiver（即"你的帖子有新评论了"通知）。
 * 如果要精确定位楼层回复的接收人，可以通过 {@link ForumService}（已通过 {@code @DubboReference} 注入）
 * 增加一个按 commentId 查询评论详情的方法来查出被回复评论的作者，再据此完善本逻辑。</p>
 *
 * <p>消费幂等说明：当前 t_notification 表未对 (receiver_id, source_id) 做唯一约束，
 * MQ 因网络抖动或消费失败重试时会导致重复插入通知（对用户体验影响较小，可接受）。
 * 生产环境建议引入消息去重表（记录已处理的 msgId 或业务唯一键）防止重复通知造成打扰。</p>
 */
@Component
@RocketMQMessageListener(
        topic = "CommentCreatedEvent",
        consumerGroup = "CommentCreatedEvent_NOTIFY_CG",
        consumeMode = ConsumeMode.CONCURRENTLY)
public class CommentCreatedEventListener implements RocketMQListener<CommentCreatedEvent> {

    private static final Logger log = LoggerFactory.getLogger(CommentCreatedEventListener.class);

    @DubboReference
    private ForumService forumService;

    private final NotifyBizService notifyBizService;

    public CommentCreatedEventListener(NotifyBizService notifyBizService) {
        this.notifyBizService = notifyBizService;
    }

    @Override
    public void onMessage(CommentCreatedEvent event) {
        try {
            TopicDTO topic = forumService.getTopicById(event.getTopicId());
            if (topic == null) {
                log.warn("CommentCreatedEvent 对应的帖子不存在，忽略：topicId={}", event.getTopicId());
                return;
            }
            // 简化实现：一律通知帖子作者。若要精确定位楼层回复的接收人，
            // 需要按 event.getReplyToId() 查询被回复评论的作者（可扩展 ForumService 增加该方法）。
            Long receiverId = topic.getAuthorId();
            if (receiverId == null || receiverId.equals(event.getAuthorId())) {
                // 帖子作者自己评论自己的帖子，不需要通知
                return;
            }
            String content = "你的帖子《" + topic.getTitle() + "》有了新的评论";
            String linkUrl = "/topic/" + topic.getId() + "#comment-" + event.getCommentId();
            notifyBizService.createNotification(receiverId, "COMMENT_REPLY", content, linkUrl);
        } catch (Exception e) {
            log.error("处理 CommentCreatedEvent 失败：{}", event, e);
            throw e;
        }
    }
}
