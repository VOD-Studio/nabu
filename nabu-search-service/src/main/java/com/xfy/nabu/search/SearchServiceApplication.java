package com.xfy.nabu.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 搜索服务启动类。职责：帖子全文搜索（Elasticsearch）。
 * 索引写入完全走 "MySQL binlog -> Canal -> RocketMQ -> search-service 消费" 链路，
 * 业务代码里不做同步双写，本服务只负责消费同步事件与提供查询能力。
 * 对外通过 Dubbo 暴露 {@link com.xfy.nabu.api.search.service.SearchService}。
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.search", "com.xfy.nabu.common"})
public class SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }
}
