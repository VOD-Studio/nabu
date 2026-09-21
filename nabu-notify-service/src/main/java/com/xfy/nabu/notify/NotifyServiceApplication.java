package com.xfy.nabu.notify;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 通知服务启动类。职责：@提醒、回复通知、点赞通知、系统通知。
 * 核心是消费 RocketMQ 事件（CommentCreatedEvent / TopicLikedEvent / UserFollowedEvent）异步生成通知，
 * 对外通过 Dubbo 暴露 {@link com.xfy.nabu.api.notify.service.NotifyService} 供 nabu-web 拉取通知列表 / 标记已读。
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.notify", "com.xfy.nabu.common"})
@MapperScan("com.xfy.nabu.notify.mapper")
public class NotifyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotifyServiceApplication.class, args);
    }
}
