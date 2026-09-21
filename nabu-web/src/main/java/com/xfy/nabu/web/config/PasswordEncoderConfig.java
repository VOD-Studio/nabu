package com.xfy.nabu.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置。
 *
 * <p>本模块只引入 spring-security-crypto 依赖用于密码哈希，不引入完整的 Spring Security，
 * 因此这里不做任何认证/鉴权配置，仅仅注册一个 {@link BCryptPasswordEncoder} Bean，
 * 供 {@link com.xfy.nabu.web.controller.AuthController} 在用户注册时对明文密码做哈希后再通过
 * Dubbo 调用 {@link com.xfy.nabu.api.user.service.UserService#register} 落库。</p>
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
