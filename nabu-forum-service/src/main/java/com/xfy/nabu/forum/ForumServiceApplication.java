package com.xfy.nabu.forum;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 论坛服务启动类。职责：版块(board)、帖子(topic)、评论(comment)。
 * 对外通过 Dubbo 暴露 {@link com.xfy.nabu.api.forum.service.ForumService}。
 * <p>
 * 本服务是全项目 Seata AT 模式分布式事务、RocketMQ 事件发布的主战场：
 * 发帖/评论后异步发布事件通知审核服务；购买置顶时同步跨服务扣积分并要求强一致。
 * <p>
 * {@code @EnableMethodCache} 激活 JetCache 的 {@code @Cached} 方法级缓存注解（帖子详情二级缓存）。
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.forum", "com.xfy.nabu.common"})
@MapperScan("com.xfy.nabu.forum.mapper")
@EnableMethodCache(basePackages = "com.xfy.nabu.forum")
public class ForumServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumServiceApplication.class, args);
    }
}
