package com.xfy.nabu.stat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 统计服务启动类。职责：话题 PV/UV 统计、热门排行榜。
 * 本模块不接入 MySQL，String/HyperLogLog/ZSet 均直接落 Redis。
 * 对外通过 Dubbo 暴露 {@link com.xfy.nabu.api.stat.service.StatService}。
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.stat", "com.xfy.nabu.common"})
public class StatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatServiceApplication.class, args);
    }
}
