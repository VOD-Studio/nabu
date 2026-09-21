package com.xfy.nabu.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 对外 BFF/HTTP API 层启动类。
 *
 * <p>nabu-web 是所有前端 HTTP 请求的统一入口，外部通过 Higress 网关路由到这里，
 * 是唯一对普通用户开放 HTTP 接口的服务（nabu-admin-service 是给管理端用的）。
 * 本服务只作为 Dubbo Consumer 转发调用各微服务的 Dubbo 接口并聚合响应，
 * 本身不直接连任何数据存储。</p>
 *
 * <p>{@code scanBasePackages} 中包含 {@code com.xfy.nabu.common}，
 * 这样 nabu-common 里的 {@code GlobalExceptionHandler}（{@code @RestControllerAdvice}）会被自动扫描到。</p>
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.web", "com.xfy.nabu.common"})
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
