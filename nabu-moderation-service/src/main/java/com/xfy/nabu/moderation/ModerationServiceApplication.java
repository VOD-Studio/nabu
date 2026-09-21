package com.xfy.nabu.moderation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 内容审核服务启动类。职责：
 * <ul>
 *   <li>同步预检：通过 Dubbo 暴露 {@link com.xfy.nabu.api.moderation.service.ModerationService}，
 *       供 forum-service 在发帖/评论前做快速敏感词拦截。</li>
 *   <li>异步复核：消费 forum-service 发布的 TopicCreatedEvent / CommentCreatedEvent，
 *       落审核日志表，作为"发帖之后仍能被异步发现违规内容"的兜底链路。</li>
 * </ul>
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.moderation", "com.xfy.nabu.common"})
@MapperScan("com.xfy.nabu.moderation.mapper")
public class ModerationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModerationServiceApplication.class, args);
    }
}
