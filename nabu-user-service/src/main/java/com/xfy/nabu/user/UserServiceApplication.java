package com.xfy.nabu.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户服务启动类。职责：用户账号、资料、积分、等级。
 * 对外通过 Dubbo 暴露 {@link com.xfy.nabu.api.user.service.UserService}。
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.user", "com.xfy.nabu.common"})
@MapperScan("com.xfy.nabu.user.mapper")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
