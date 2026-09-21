package com.xfy.nabu.task;

import com.xfy.nabu.api.stat.dto.HotTopicDTO;
import com.xfy.nabu.api.stat.service.StatService;
import java.util.List;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 热门榜快照刷新任务。
 *
 * <p>本模块是 SchedulerX 的本地替代方案：如果要接入真实 SchedulerX，
 * 需要引入 {@code com.aliyun.schedulerx2:schedulerx2-spring-boot-starter} 并绑定阿里云 AccessKey/命名空间，
 * 再把此处的 {@code @Scheduled} 方法迁移为 SchedulerX 的 JavaProcessor 任务。</p>
 *
 * <p>周期由 {@code task.hot-topic-refresh.cron} 配置，默认每 5 分钟执行一次，
 * 通过 {@link StatService#topHotTopics(int)} 拉取当前热门帖子榜单。</p>
 */
@Component
public class HotTopicRefreshTask {

    private static final Logger log = LoggerFactory.getLogger(HotTopicRefreshTask.class);

    @DubboReference
    private StatService statService;

    /**
     * 刷新热门榜快照。
     *
     * <p>这里只是把拉取到的榜单打印日志，模拟"刷新热门榜快照"的效果。
     * 真实场景下可以扩展为：把榜单写入 Redis 供首页直接读取，
     * 或者通过 RocketMQ 推送给前端/搜索服务做进一步处理。</p>
     */
    @Scheduled(cron = "${task.hot-topic-refresh.cron:0 */5 * * * ?}")
    public void refreshHotTopics() {
        List<HotTopicDTO> hotTopics = statService.topHotTopics(20);
        log.info("[热门榜快照刷新] 本次拉取到 {} 条热门帖子: {}", hotTopics.size(), hotTopics);
        // TODO: 扩展方向：将 hotTopics 写入 Redis（例如 nabu:hot:topics 快照 key），
        // 或者通过 RocketMQ 发布 HotTopicRefreshedEvent 供其他服务消费。
    }
}
