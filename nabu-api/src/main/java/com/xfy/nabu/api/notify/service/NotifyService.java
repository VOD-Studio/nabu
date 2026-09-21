package com.xfy.nabu.api.notify.service;

import com.xfy.nabu.api.notify.dto.NotificationDTO;
import com.xfy.nabu.common.result.PageResult;

/**
 * 通知服务 Dubbo 接口。主要通过消费 RocketMQ 事件（CommentCreatedEvent / TopicLikedEvent / UserFollowedEvent 等）
 * 异步生成通知，此接口用于 nabu-web 拉取通知列表 / 标记已读等主动查询场景。
 */
public interface NotifyService {

    PageResult<NotificationDTO> listByReceiver(Long receiverId, long pageNum, long pageSize);

    long countUnread(Long receiverId);

    void markRead(Long receiverId, Long notificationId);

    void markAllRead(Long receiverId);
}
