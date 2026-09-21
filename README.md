# Nabu

一个基于 **Java 21 + Spring Boot 3.5 + Dubbo 3.3 + Spring Cloud Alibaba 全家桶 + RustFS + Docker Compose**
的完整微服务论坛项目。目标不是做一个最简化的示例，而是尽可能把企业级 Java 微服务中的
各种模式实际用一遍：RPC、注册发现、配置中心、限流熔断、分布式事务、消息队列、多级缓存、
CDC 同步、全文搜索、对象存储、网关、可观测性……

## 技术栈与版本线（2026-09 校验过 Maven Central / Docker Hub 的真实可用版本）

| 分类 | 技术 | 版本 |
|---|---|---|
| JVM | Java | 21 LTS |
| 基础框架 | Spring Boot | 3.5.6 |
| 微服务生态 | Spring Cloud | 2025.0.0 |
| 微服务生态 | Spring Cloud Alibaba | 2025.0.0.0 |
| RPC | Apache Dubbo | 3.3.6（Triple 协议） |
| 注册/配置中心 | Nacos | 3.0.3 |
| 限流熔断 | Sentinel | 1.8.9（随 SCA BOM 管理） |
| 分布式事务 | Seata | 2.5.0（groupId 已迁移为 `org.apache.seata`） |
| 消息队列 | RocketMQ | 5.3.1（Server）/ rocketmq-spring-boot-starter 2.3.5 |
| 数据库 | MySQL | 8.4 LTS |
| ORM | MyBatis | mybatis-spring-boot-starter 3.0.4 |
| 连接池 | Druid | 1.2.28（druid-spring-boot-3-starter） |
| 缓存 | Redis 7 + JetCache | 2.7.9 |
| CDC | Canal | server latest（本地演示，简化消息结构） |
| 搜索 | Elasticsearch | 8.18.1 |
| 对象存储 | RustFS（S3 兼容） | 1.0.0（镜像 digest 固定），AWS SDK for Java v2 (2.29.52) 接入 |
| JSON | Fastjson2 | 2.0.65 |
| 上下文传播 | TTL (transmittable-thread-local) | 2.14.5 |
| 网关 | Higress | Docker All-in-One latest |
| 鉴权 | Spring Security Crypto (BCrypt) + JWT | jjwt 0.12.7 |
| 调度 | Spring @Scheduled（本地替代 SchedulerX，见 nabu-task-service 注释） |
| 可观测性 | OpenTelemetry Collector + Prometheus + Grafana + Loki + Tempo |
| DB 迁移 | Flyway |
| 测试 | JUnit + Mockito + Testcontainers 1.20.6 |
| 部署 | Docker Compose（拆分为多个 compose 文件） |

## 模块划分

```text
nabu/
├── nabu-common/          # 公共基础：Result/异常体系/TTL上下文/常量
├── nabu-api/              # Dubbo 服务契约（各 service 接口 + DTO），Provider/Consumer 共同依赖
├── nabu-auth-service/     # 登录、JWT、OAuth2 风格 access/refresh token
├── nabu-user-service/     # 用户、资料、积分、等级
├── nabu-forum-service/    # 板块、帖子、评论（Seata AT + RocketMQ 事件演示核心）
├── nabu-social-service/   # 点赞、收藏、关注、签到（Redis Set/Bitmap 演示核心）
├── nabu-notify-service/   # @、回复、点赞、系统通知（RocketMQ 消费方）
├── nabu-search-service/   # Elasticsearch 全文搜索（Canal->MQ->ES 链路消费方）
├── nabu-file-service/     # RustFS 文件上传/下载/预签名直传
├── nabu-moderation-service/ # 内容审核（同步预检 + 异步复核）
├── nabu-stat-service/     # PV/UV/热门排行（Redis String/HyperLogLog/ZSet 演示核心）
├── nabu-task-service/     # 定时任务（本地替代 SchedulerX）
├── nabu-admin-service/    # 后台管理聚合（瘦 HTTP 层，聚合调用其他 Dubbo 服务）
├── nabu-web/              # 对外 HTTP API / BFF，唯一面向普通用户的入口
└── deploy/                # Docker Compose 全部编排文件与配置
```

内部服务间统一用 **Dubbo Triple** 协议调用；对外（浏览器/客户端）统一走 **HTTP REST**
（`nabu-web` 是唯一入口，前面挂 Higress 网关）。

## 服务端口一览

| 服务 | HTTP 端口 | Dubbo(Triple) 端口 |
|---|---|---|
| nabu-web | 18080 | — |
| nabu-user-service | 18081 | 28081 |
| nabu-auth-service | 18082 | 28082 |
| nabu-forum-service | 18083 | 28083 |
| nabu-social-service | 18084 | 28084 |
| nabu-notify-service | 18085 | 28085 |
| nabu-search-service | 18086 | 28086 |
| nabu-file-service | 18087 | 28087 |
| nabu-moderation-service | 18088 | 28088 |
| nabu-stat-service | 18089 | 28089 |
| nabu-task-service | 18090 | 28090 |
| nabu-admin-service | 18091 | 28091 |

## 编译

```bash
mvn -B -DskipTests package          # 全量：14 个 jar（12 个可执行 fat jar + 2 个库 jar）
mvn -B -pl nabu-user-service -am package -DskipTests   # 只构建某个服务及其依赖模块
```

产物在各模块自己的 `target/` 下，例如 `nabu-web/target/nabu-web-0.1.0-SNAPSHOT.jar`。
`nabu-common` / `nabu-api` 是普通库 jar，不能 `java -jar`。
同目录下的 `*.jar.original` 是 repackage 之前的瘦 jar，不要拿去运行。

## 本地启动

> **顺序要求**：必须先起 Nacos（注册中心 + 配置中心），再起各 Java 服务。
> 各服务的 `application.yml` 里有 `spring.config.import: optional:nacos:<服务名>.yaml`，
> 该 Nacos dataId 不存在也不影响启动（`optional:` 前缀），但 Nacos 本身连不上会让服务
> 卡在启动阶段反复重连。

### 1. 只起依赖，裸机跑 Java 服务（推荐日常开发调试）

```bash
cd deploy
./scripts/up.sh obs   # 拉起 MySQL/Redis/ES/RustFS + Nacos/Sentinel/RocketMQ/Seata/Canal/Higress + 可观测性栈
```

各服务 `application.yml` 里的默认值就是 `127.0.0.1:<容器映射端口>`，裸机跑
`mvn -pl nabu-user-service spring-boot:run` 之类命令即可直接连上。

只想先验证链路，可以单独起 Nacos（standalone 内嵌 derby，无需外部数据库）：

```bash
cd deploy
docker compose --env-file .env -f compose.yml -f compose.middleware.yml up -d nacos
```

**宿主机端口避让**：本机 6379 / 8443 已被其他项目容器占用，因此做了两处偏移——

| 组件 | 原始映射 | 现映射 | 影响 |
|---|---|---|---|
| nabu-redis | `6379:6379` | `6380:6379` | 裸机默认端口同步改为 6380；容器网络内仍是 `redis:6379`（见 `.env`） |
| nabu-higress | `8443:8443` | `18443:8443` | 容器内监听端口不变，只是宿主访问 HTTPS 走 18443 |

如果换台机器这两个端口是空的，把映射改回 `6379:6379` / `8443:8443`，
同时把各 `application.yml` 里的 `${REDIS_PORT:6380}` 默认值改回 `6379` 即可。

### 2. 全量 Docker Compose（包含全部 12 个 Java 服务镜像构建 + 启动）

```bash
cd deploy
./scripts/up.sh all
```

首次构建需要下载基础镜像与 Maven 依赖，比较慢；12 个镜像共用同一份 Dockerfile 与
`/root/.m2` 构建缓存（BuildKit cache mount，见根目录 `Dockerfile` 与 `.dockerignore`），
只有第一个镜像需要真正下载依赖。

**容器化运行的三点注意**

1. **内存**：`compose.app.yml` 给每个应用容器设了 `mem_limit: ${APP_MEM_LIMIT:-1g}` 和
   `JAVA_OPTS=${JAVA_OPTS:--XX:MaxRAMPercentage=60 -XX:+ExitOnOutOfMemoryError}`。
   12 个全起 + 基础设施 + 中间件 + 可观测性栈仍然需要 8G 以上，Docker Desktop 内存不足时
   按需起子集：`docker compose ... up -d nabu-user-service nabu-web`。
2. **就绪门控**：`depends_on` 已带 `condition` —— MySQL/Redis/ES/RustFS/Nacos 有 healthcheck，
   用 `service_healthy` 等真正可用；RocketMQ/Seata 镜像没有 healthcheck，只能 `service_started`，
   首次联调若遇到连接报错，等 broker 起来后重启对应服务即可。
3. **不要裸机与容器混跑**：`compose.app.yml` 只映射 HTTP `1808x`，没有映射 Dubbo Triple 的
   `2808x`，容器内 provider 注册到 Nacos 的是 `172.x` 内网地址，宿主机进程访问不到。
   要混跑就补映射并用 `DUBBO_IP_TO_REGISTRY` 指定注册地址，否则统一选一种方式。

查看某个服务日志：

```bash
./scripts/logs.sh nabu-user-service
```

停止：

```bash
./scripts/down.sh
```

### 3. 关键管理入口

| 用途 | 地址 |
|---|---|
| Nacos 控制台 | http://localhost:8849/（Nacos 3 独立控制台；服务 API 仍为 8848） |
| Sentinel Dashboard | http://localhost:8858（sentinel/sentinel） |
| RocketMQ Dashboard | http://localhost:9878 |
| Seata（TC）控制端口 | localhost:7091 |
| RustFS Console | http://localhost:9001 |
| Elasticsearch | http://localhost:9200 |
| Grafana | http://localhost:3000（admin/admin123） |
| Prometheus | http://localhost:9090 |
| Higress Console | http://localhost:8001 |

## 核心链路导航

- **Seata AT 分布式事务**：`ForumService.pinTopicWithPoints`（forum-service 发起，调用 user-service 扣积分）。
- **RocketMQ 事件驱动**：`TopicCreatedEvent` / `CommentCreatedEvent` / `TopicLikedEvent` / `UserFollowedEvent`
  的生产方与消费方分布在 forum/social/notify/moderation/search 各服务中，可以顺着 topic 名搜索代码。
- **Redis 全数据结构演示**：social-service（Set/Bitmap）、stat-service（String/HyperLogLog/ZSet）。
- **多级缓存**：user-service 用 JetCache（Caffeine L1 + Redis L2）。
- **CDC 简化演示**：search-service 消费的 `CanalBinlogSyncEvent`（真实 Canal 协议更复杂，见该模块类注释）。
- **对象存储直传**：file-service 通过 AWS S3 SDK 生成 RustFS 预签名 URL，客户端可绕过 Java 服务直传大文件。

## 数据库连接约定

各服务使用独立 MySQL 实例（本地端口 3307/3308/3310/3311/3312，容器内统一 3306），
账号密码默认均为 `nabu` / `nabu123456`（仅用于本地/开发环境，生产环境务必更换）。

## 说明

本仓库定位为工程实践参考，部分实现做了有意的简化（例如敏感词过滤、Canal 消息结构、
SchedulerX 本地替代方案），代码中都留有中文注释说明"生产环境应该怎么做"，方便按需扩展为
真实可用的实现。
