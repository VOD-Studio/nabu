package com.xfy.nabu.user.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 首次启动先迁移数据库，再创建会被 Seata 代理的数据源，避免 undo_log 检查早于建表。 */
@Configuration(proxyBeanMethods = false)
public class DatabaseMigrationConfig {

    @Bean(initMethod = "migrate")
    Flyway flyway(DataSourceProperties properties) {
        // 使用独立的迁移连接，不能提前获取仍在等待迁移的数据源。
        return Flyway.configure()
                .dataSource(properties.getUrl(), properties.getUsername(), properties.getPassword())
                .load();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid")
    DruidDataSource dataSource(DataSourceProperties properties, Flyway flyway) {
        // flyway 注入前已完成 migrate；此后 Seata 才能检查并代理数据源。
        return properties
                .initializeDataSourceBuilder()
                .type(DruidDataSource.class)
                .build();
    }
}
