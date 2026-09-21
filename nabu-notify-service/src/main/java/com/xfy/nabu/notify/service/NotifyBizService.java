package com.xfy.nabu.notify.service;

import com.xfy.nabu.api.notify.dto.NotificationDTO;
import com.xfy.nabu.common.result.PageResult;

/**
 * 内部业务服务接口（区别于对外的 Dubbo {@code NotifyService}），供本模块内部
 * （Dubbo 实现类 / RocketMQ 消费者）复用。
 */
public interface NotifyBizService {

    PageResult<NotificationDTO> listByReceiver(Long receiverId, long pageNum, long pageSize);

    long countUnread(Long receiverId);

    void markRead(Long receiverId, Long notificationId);

    void markAllRead(Long receiverId);

    /**
     * 生成一条通知，供 RocketMQ 消费者调用。
     *
     * @param receiverId 接收人 id
     * @param type       通知类型：COMMENT_REPLY / LIKE / MENTION / SYSTEM / FOLLOW
     * @param content    通知文案
     * @param linkUrl    跳转链接
     */
    void createNotification(Long receiverId, String type, String content, String linkUrl);
}
