package com.xfy.nabu.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务服务启动类。
 *
 * <p>本模块是阿里云 SchedulerX 的本地替代方案：原设计希望用 SchedulerX 做分布式定时任务调度，
 * 但 SchedulerX 需要绑定阿里云账号的 AccessKey/命名空间，当前未接入阿里云账号，无法直接使用，
 * 因此改用 Spring 原生 {@code @EnableScheduling} + {@code @Scheduled} 实现示例定时任务。
 * 如果要接入真实 SchedulerX，需要引入 {@code com.aliyun.schedulerx:schedulerx2-spring-boot-starter}
 * 并在阿里云控制台创建命名空间、绑定 AccessKey，再把本模块里的 {@code @Scheduled} 方法
 * 迁移为 SchedulerX 的 JavaProcessor。</p>
 *
 * <p>本模块只作为 Dubbo Consumer 消费其他服务（例如 {@link com.xfy.nabu.api.stat.service.StatService}），
 * 不对外提供任何 Dubbo 服务。</p>
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.task", "com.xfy.nabu.common"})
@EnableScheduling
public class TaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }
}
