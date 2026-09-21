package com.xfy.nabu.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证服务启动类。职责：登录、JWT 签发/校验/续期、登出。
 * 对外通过 Dubbo 暴露 {@link com.xfy.nabu.api.auth.service.AuthService}，
 * 密码校验通过 @DubboReference 调用 user-service 的 UserService.getPasswordHashByUsername。
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.auth", "com.xfy.nabu.common"})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
