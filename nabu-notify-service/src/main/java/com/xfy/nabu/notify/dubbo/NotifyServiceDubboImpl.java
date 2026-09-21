package com.xfy.nabu.notify.dubbo;

import com.xfy.nabu.api.notify.dto.NotificationDTO;
import com.xfy.nabu.api.notify.service.NotifyService;
import com.xfy.nabu.common.result.PageResult;
import com.xfy.nabu.notify.service.NotifyBizService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * Dubbo Provider：对外暴露 {@link NotifyService}，内部转发给 {@link NotifyBizService}。
 * 这样拆分是为了让业务逻辑不直接依赖 Dubbo 注解，方便未来暴露 REST 管理接口时复用同一套业务代码。
 */
@DubboService
public class NotifyServiceDubboImpl implements NotifyService {

    private final NotifyBizService notifyBizService;

    public NotifyServiceDubboImpl(NotifyBizService notifyBizService) {
        this.notifyBizService = notifyBizService;
    }

    @Override
    public PageResult<NotificationDTO> listByReceiver(Long receiverId, long pageNum, long pageSize) {
        return notifyBizService.listByReceiver(receiverId, pageNum, pageSize);
    }

    @Override
    public long countUnread(Long receiverId) {
        return notifyBizService.countUnread(receiverId);
    }

    @Override
    public void markRead(Long receiverId, Long notificationId) {
        notifyBizService.markRead(receiverId, notificationId);
    }

    @Override
    public void markAllRead(Long receiverId) {
        notifyBizService.markAllRead(receiverId);
    }
}
