package com.xfy.nabu.web.controller;

import com.xfy.nabu.api.notify.dto.NotificationDTO;
import com.xfy.nabu.api.notify.service.NotifyService;
import com.xfy.nabu.common.result.PageQuery;
import com.xfy.nabu.common.result.PageResult;
import com.xfy.nabu.common.result.Result;
import com.xfy.nabu.common.util.TraceContext;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知接口：分页查询 / 未读数 / 标记已读。均为薄转发层，调用 {@link NotifyService} 完成实际业务逻辑，
 * 接收人 id 取自 {@link TraceContext#getUserId()}（由 JWT 拦截器写入）。
 */
@RestController
public class NotifyController {

    @DubboReference
    private NotifyService notifyService;

    /**
     * 分页查询当前用户的通知列表。
     */
    @GetMapping("/api/v1/notifications")
    public Result<PageResult<NotificationDTO>> list(PageQuery pageQuery) {
        Long userId = TraceContext.getUserId();
        PageResult<NotificationDTO> pageResult = notifyService.listByReceiver(userId, pageQuery.getPageNum(), pageQuery.getPageSize());
        return Result.success(pageResult);
    }

    /**
     * 查询当前用户未读通知数。
     */
    @GetMapping("/api/v1/notifications/unread-count")
    public Result<Long> unreadCount() {
        Long userId = TraceContext.getUserId();
        long count = notifyService.countUnread(userId);
        return Result.success(count);
    }

    /**
     * 标记单条通知已读。
     */
    @PostMapping("/api/v1/notifications/{id}/read")
    public Result<Void> markRead(@PathVariable("id") Long id) {
        Long userId = TraceContext.getUserId();
        notifyService.markRead(userId, id);
        return Result.success();
    }

    /**
     * 标记全部通知已读。
     */
    @PostMapping("/api/v1/notifications/read-all")
    public Result<Void> markAllRead() {
        Long userId = TraceContext.getUserId();
        notifyService.markAllRead(userId);
        return Result.success();
    }
}
