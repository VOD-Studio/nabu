package com.xfy.nabu.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 历史通知清理任务（演示占位）。
 *
 * <p>本模块是 SchedulerX 的本地替代方案：如果要接入真实 SchedulerX，
 * 需要引入 {@code com.aliyun.schedulerx2:schedulerx2-spring-boot-starter} 并绑定阿里云 AccessKey/命名空间。</p>
 *
 * <p>每天凌晨 2 点执行一次。由于 task-service 没有直接访问 {@code nabu_notify} 数据库的权限，
 * 这里只记录一条日志说明清理逻辑应该如何落地：</p>
 *
 * <p>TODO: 生产环境这里会调用 {@link com.xfy.nabu.api.notify.service.NotifyService}
 * 提供的批量清理接口，或者直接操作 nabu_notify 数据库归档历史通知。
 * 但当前 {@code NotifyService} 契约（见 nabu-api）尚未暴露批量清理方法，
 * 可作为后续扩展，给 {@code NotifyService} 增加一个
 * {@code cleanupBefore(LocalDateTime before)} 方法，
 * 再通过 {@code @DubboReference} 调用它完成真正的清理。</p>
 */
@Component
public class NotificationCleanupTask {

    private static final Logger log = LoggerFactory.getLogger(NotificationCleanupTask.class);

    @Scheduled(cron = "${task.notification-cleanup.cron:0 0 2 * * ?}")
    public void cleanupExpiredNotifications() {
        log.info("[历史通知清理] 触发清理任务，但 task-service 未直接持有 nabu_notify 数据库权限，"
                + "当前仅作为占位演示：生产环境应调用 NotifyService 的批量清理接口或直接归档历史通知数据。");
        // TODO: 待 NotifyService 增加 cleanupBefore(LocalDateTime before) 方法后，
        // 在这里通过 @DubboReference NotifyService notifyService 调用：
        // notifyService.cleanupBefore(LocalDateTime.now().minusDays(90));
    }
}
