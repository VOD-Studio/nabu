# Changelog

All notable changes to Nabu will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- 初始化 Nabu 微服务框架骨架：Java 21 + Spring Boot 3.5 + Dubbo 3.3，Maven 多模块（14 模块）+ Docker Compose 部署。
- 引入 Spotless（palantir-java-format）统一 Java 代码格式，4 空格缩进、120 列、LF 换行。
- 新增 `AGENTS.md` 仓库贡献指南，明确 Conventional Commits 与每功能点一提交规范。
- 补齐 `micrometer-registry-prometheus` 依赖，使 `/actuator/prometheus` 端点可用。

### Fixed

- 修复 `mvn compile` 失败：`nabu-web` 缺少 `spring-web` 依赖，`dubbo-bom` 将 Spring 降级到 5.3.39。
- 修复本地 Nacos 地址配置与端口映射问题。
- 修复基础组件（Nacos、MySQL、Redis、Seata、Sentinel）Docker 启动配置与健康检查。
- 修复 Flyway 迁移在 Druid/Seata 代理数据源环境下的连接丢失问题，改用独立连接并在代理创建前完成迁移。
- 补齐 Sentinel Nacos 规则数据源依赖，修复运行时缺失。
- 修复 Dubbo Provider 未扫描的问题，启用 Nacos 注册中心地址。
- 完善中间件就绪门控逻辑，支持小内存环境分组启动。
- 修复 `NoResourceFoundException` 被误报为系统异常的问题。

### Changed

- 优化 Docker 构建缓存策略与 Compose 资源门控配置。
