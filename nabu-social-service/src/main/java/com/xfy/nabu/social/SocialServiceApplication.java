package com.xfy.nabu.social;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 社交服务启动类。职责：点赞、收藏、关注、签到。
 * 本模块不接入 MySQL，所有数据结构直接落 Redis（Set/Bitmap 既是存储也是索引）。
 * 对外通过 Dubbo 暴露 {@link com.xfy.nabu.api.social.service.SocialService}。
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.social", "com.xfy.nabu.common"})
public class SocialServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialServiceApplication.class, args);
    }
}
