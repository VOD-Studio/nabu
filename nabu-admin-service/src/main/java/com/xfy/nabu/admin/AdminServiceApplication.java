package com.xfy.nabu.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 后台管理聚合服务启动类。
 *
 * <p>本模块是一个瘦聚合层，本身不提供任何 Dubbo 服务，只作为 Dubbo Consumer
 * 通过 {@code @DubboReference} 汇总调用其他服务，供管理端 HTTP 接口使用。
 * 对外 REST 接口走 Higress 网关单独路由（例如 /admin/**），与面向普通用户的 nabu-web 区分开。</p>
 *
 * <p>{@code scanBasePackages} 中包含 {@code com.xfy.nabu.common}，
 * 这样 nabu-common 里的 {@code GlobalExceptionHandler}（{@code @RestControllerAdvice}）会被自动扫描到，无需额外配置。</p>
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.admin", "com.xfy.nabu.common"})
public class AdminServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }
}
