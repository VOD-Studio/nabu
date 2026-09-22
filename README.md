# Nabu

一个基于 **Java 21 + Spring Boot 3.5 + Dubbo 3.3 + Spring Cloud Alibaba 全家桶 + RustFS + Docker Compose**
的完整微服务论坛项目。目标不是做一个最简化的示例，而是尽可能把企业级 Java 微服务中的
各种模式实际用一遍：RPC、注册发现、配置中心、限流熔断、分布式事务、消息队列、多级缓存、
CDC 同步、全文搜索、对象存储、网关、可观测性……

## 技术栈与版本线（2026-09 校验过 Maven Central / Docker Hub 的真实可用版本）

| 分类          | 技术                                                                | 版本                                                          |
| ------------- | ------------------------------------------------------------------- | ------------------------------------------------------------- |
| JVM           | Java                                                                | 21 LTS                                                        |
| 基础框架      | Spring Boot                                                         | 3.5.6                                                         |
| 微服务生态    | Spring Cloud                                                        | 2025.0.0                                                      |
| 微服务生态    | Spring Cloud Alibaba                                                | 2025.0.0.0                                                    |
| RPC           | Apache Dubbo                                                        | 3.3.6（Triple 协议）                                          |
| 注册/配置中心 | Nacos                                                               | 3.0.3                                                         |
| 限流熔断      | Sentinel                                                            | 1.8.9（随 SCA BOM 管理）                                      |
| 分布式事务    | Seata                                                               | 2.5.0（groupId 已迁移为 `org.apache.seata`）                  |
| 消息队列      | RocketMQ                                                            | 5.3.1（Server）/ rocketmq-spring-boot-starter 2.3.5           |
| 数据库        | MySQL                                                               | 8.4 LTS                                                       |
| ORM           | MyBatis                                                             | mybatis-spring-boot-starter 3.0.4                             |
| 连接池        | Druid                                                               | 1.2.28（druid-spring-boot-3-starter）                         |
| 缓存          | Redis 7 + JetCache                                                  | 2.7.9                                                         |
| CDC           | Canal                                                               | server latest（本地演示，简化消息结构）                       |
| 搜索          | Elasticsearch                                                       | 8.18.1                                                        |
| 对象存储      | RustFS（S3 兼容）                                                   | 1.0.0（镜像 digest 固定），AWS SDK for Java v2 (2.29.52) 接入 |
| JSON          | Fastjson2                                                           | 2.0.65                                                        |
| 上下文传播    | TTL (transmittable-thread-local)                                    | 2.14.5                                                        |
| 网关          | Higress                                                             | Docker All-in-One latest                                      |
| 鉴权          | Spring Security Crypto (BCrypt) + JWT                               | jjwt 0.12.7                                                   |
| 调度          | Spring @Scheduled（本地替代 SchedulerX，见 nabu-task-service 注释） |
| 可观测性      | OpenTelemetry Collector + Prometheus + Grafana + Loki + Tempo       |
| DB 迁移       | Flyway                                                              |
| 测试          | JUnit + Mockito + Testcontainers 1.20.6                             |
| 部署          | Docker Compose（拆分为多个 compose 文件）                           |

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

| 服务                    | HTTP 端口 | Dubbo(Triple) 端口 |
| ----------------------- | --------- | ------------------ |
| nabu-web                | 18080     | —                  |
| nabu-user-service       | 18081     | 28081              |
| nabu-auth-service       | 18082     | 28082              |
| nabu-forum-service      | 18083     | 28083              |
| nabu-social-service     | 18084     | 28084              |
| nabu-notify-service     | 18085     | 28085              |
| nabu-search-service     | 18086     | 28086              |
| nabu-file-service       | 18087     | 28087              |
| nabu-moderation-service | 18088     | 28088              |
| nabu-stat-service       | 18089     | 28089              |
| nabu-task-service       | 18090     | 28090              |
| nabu-admin-service      | 18091     | 28091              |

## 容器清单

本地编排拆成 5 个 compose 文件，按依赖层次叠加（`compose.yml` 只声明公共网络 `nabu-net`）：

| Compose 文件                 | 层       | 内容                                                       |
| ---------------------------- | -------- | ---------------------------------------------------------- |
| `compose.infrastructure.yml` | 基础设施 | MySQL（按域拆 5 个实例）/ Redis / Elasticsearch / RustFS   |
| `compose.middleware.yml`     | 中间件   | Nacos / Sentinel / RocketMQ / Seata / Canal / Higress     |
| `compose.observability.yml`  | 可观测性 | OpenTelemetry Collector / Prometheus / Loki / Tempo / Grafana |
| `compose.app.yml`            | 应用     | 12 个 Java 微服务（见上方"服务端口一览"）                 |

### 基础设施（compose.infrastructure.yml）

| 容器名               | 镜像               | 宿主端口   | 用途                                                          |
| -------------------- | ------------------- | ---------- | ------------------------------------------------------------- |
| nabu-mysql-user      | mysql:8.4           | 3307       | 用户域私有库 `nabu_user`                                      |
| nabu-mysql-forum     | mysql:8.4           | 3308       | 论坛域私有库 `nabu_forum`，Canal binlog 监听对象              |
| nabu-mysql-notify    | mysql:8.4           | 3310       | 通知域私有库 `nabu_notify`                                    |
| nabu-mysql-file      | mysql:8.4           | 3311       | 文件元数据私有库 `nabu_file`                                  |
| nabu-mysql-moderation| mysql:8.4           | 3312       | 审核域私有库 `nabu_moderation`                                |
| nabu-redis           | redis:7-alpine      | 6380       | 缓存/分布式锁/Bitmap/ZSet（宿主避让 6379，见下文"端口避让"）  |
| nabu-elasticsearch   | elasticsearch:8.18.1| 9200       | 全文搜索（单节点，关闭 xpack 安全）                           |
| nabu-rustfs          | rustfs:1.0.0        | 9000、9001 | S3 兼容对象存储，file-service 用 AWS S3 SDK 接入；9001 为控制台 |

> 每个业务域独立一个 MySQL 实例，模拟微服务"数据库私有"原则；资源紧张时可合并为一个 MySQL + 多 database。

### 中间件（compose.middleware.yml）

| 容器名                 | 镜像                          | 宿主端口             | 用途                                                        |
| ---------------------- | ----------------------------- | -------------------- | ----------------------------------------------------------- |
| nabu-nacos             | nacos-server:v3.0.3           | 8848、8849、9848、9849 | 注册中心 + 配置中心（standalone 内嵌 derby）；8849 为 v3 控制台 |
| nabu-sentinel-dashboard| sentinel-dashboard:1.8.9       | 8858                 | 流控/熔断规则可视化与实时监控                               |
| nabu-rmqnamesrv        | rocketmq:5.3.1                | 9876                 | RocketMQ NameServer                                         |
| nabu-rmqbroker         | rocketmq:5.3.1                | 10909、10911、10912  | RocketMQ Broker（单机）                                      |
| nabu-rmqproxy          | rocketmq:5.3.1                | 8081、8082           | RocketMQ Proxy，Producer/Consumer 统一接入                   |
| nabu-rmqdashboard      | rocketmq-dashboard            | 9878                 | RocketMQ 控制台                                             |
| nabu-seata-server      | seata-server:2.5.0            | 8091、7091           | Seata AT 分布式事务协调器（TC），注册到 Nacos；7091 为指标端口 |
| nabu-canal-server      | canal-server:latest           | 11111、11112         | 监听 mysql-forum binlog → Elasticsearch 索引同步             |
| nabu-higress           | higress all-in-one:latest     | 8001、8080、18443    | 网关 + 控制台（All-in-One 单容器）；18443 为 HTTPS           |

### 可观测性（compose.observability.yml）

各 Java 服务通过 OTLP 上报到 `otel-collector`，再分发到后端存储，Grafana 统一可视化。

| 容器名            | 镜像                    | 宿主端口       | 用途                                                         |
| ----------------- | ----------------------- | -------------- | ------------------------------------------------------------ |
| nabu-otel-collector | otel-collector-contrib  | 4317、4318、9464 | OTLP 接收（gRPC/HTTP），9464 供 Prometheus 抓取汇总指标       |
| nabu-prometheus   | prometheus:latest       | 9090           | 指标抓取与存储                                               |
| nabu-loki         | loki:3.4.2              | 3100           | 日志聚合                                                     |
| nabu-tempo        | tempo                   | 3200           | 链路追踪                                                     |
| nabu-grafana      | grafana:latest          | 3000           | 可视化面板（默认 admin/admin123）                            |

### 应用（compose.app.yml）

12 个 Java 微服务，容器名与服务名一致（如 `nabu-user-service`），全部共用根目录 `Dockerfile`，通过 `build.args.MODULE` 区分构建。端口映射见上方"服务端口一览"——仅映射 HTTP `1808x`，**不映射** Dubbo Triple `2808x`，因此不要与裸机服务混跑（详见下文"本地启动"§2 注意 3）。

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

> 环境变量放在 `deploy/.env`（含口令，已被 `.gitignore` 忽略、不入库）。仓库只提交模板
> `deploy/.env.example`；首次运行脚本时若没有 `.env` 会自动复制模板生成，请按需修改口令。
> 模板里 `NACOS_PASSWORD` / `REDIS_PASSWORD` / `JWT_SECRET` 与三个面板口令
> 已经起过 MySQL（存在 `deploy/data/mysql-*`）时不要用模板覆盖现有 `.env`。

各服务 `application.yml` 里的地址默认值就是 `127.0.0.1:<容器映射端口>`，但**数据库与对象存储的口令
没有默认值**，只能由 `deploy/.env` 提供。裸机跑之前先导出凭据（只导出 `*_PASSWORD` / `*_USERNAME` /
`RUSTFS_ACCESS_KEY` / `RUSTFS_SECRET_KEY`，不导出 `*_HOST`，所以容器地址默认值照常生效）：

```bash
source deploy/scripts/dev-env.sh
mvn -pl nabu-user-service spring-boot:run
```

漏掉这一步时服务会在启动阶段报 `Could not resolve placeholder 'MYSQL_USER_PASSWORD'`，按提示补上即可；
`./deploy/scripts/run-host.sh` / `run-host-bg.sh` 已内置该 source，不需要手动执行。

只想先验证链路，可以单独起 Nacos（standalone 内嵌 derby，无需外部数据库）：

```bash
cd deploy
docker compose --env-file .env -f compose.yml -f compose.middleware.yml up -d nacos
```

**宿主机端口避让**：本机 6379 / 8443 已被其他项目容器占用，因此做了两处偏移——

| 组件         | 原始映射    | 现映射       | 影响                                                                |
| ------------ | ----------- | ------------ | ------------------------------------------------------------------- |
| nabu-redis   | `6379:6379` | `6380:6379`  | 裸机默认端口同步改为 6380；容器网络内仍是 `redis:6379`（见 `.env`） |
| nabu-higress | `8443:8443` | `18443:8443` | 容器内监听端口不变，只是宿主访问 HTTPS 走 18443                     |

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
   按需起子集：`./scripts/up.sh all nabu-user-service nabu-web`。模式后的参数是 Compose 服务名，
   只启动这些服务及其声明的依赖；已经运行的其他服务不会自动停止。
2. **就绪门控**：`depends_on` 已带 `condition` —— MySQL/Redis/ES/RustFS/Nacos 有 healthcheck，
   MySQL 使用业务账号通过 TCP 查询业务库，Seata 检查事务端口；相关应用等待它们健康后启动。
   RocketMQ Broker 还需在 NameServer 中注册并激活，Proxy 和消费者才会继续启动。
   `Started` 只表示进程已启动，还需检查应用的 `/actuator/health` 和启动日志。
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

**约 6 GiB Docker 内存的分组联调**：切换组前先运行 `./scripts/down.sh`，它保留数据库目录和数据卷。
不要同时运行以下所有组，也不要在服务运行时并发构建全部镜像。

```bash
# 用户、认证与 BFF
./scripts/up.sh all nabu-user-service nabu-auth-service nabu-web

# 文件存储
./scripts/up.sh all nabu-file-service

# 日志、链路和管理面板
./scripts/up.sh all otel-collector grafana
```

内存有限时，在仓库根目录逐个构建镜像后再分组启动：

```bash
for module in nabu-*-service nabu-web; do
  docker build --build-arg MODULE="$module" -t "nabu-$module" . || break
done
```

首次创建空业务库时，user/forum 会先用独立连接执行 Flyway，再创建 Seata 代理数据源，
保证 `undo_log` 已存在。若旧 MySQL 初始化失败，先停止实例并完整备份对应的
`deploy/data/mysql-*` 目录，再处理恢复或重新初始化；不要直接清空有业务数据的目录。

本地 Compose 默认 `DUBBO_CONSUMER_CHECK=false`，允许消费者在部分 Provider 未启动时运行；
未启动服务对应的功能仍不可用，实际调用会报错。要检查完整 RPC 依赖，可在启动前设置
`DUBBO_CONSUMER_CHECK=true`。`NACOS_ADDRESS` 只填写主机名，端口由应用配置补齐。

RustFS 1.0.0、Tempo 3.0.0、Collector 0.161.0 的镜像使用 digest 固定。
RustFS 的四个目录在本地共用 Docker 虚拟磁盘，因此只在此开发编排中开启磁盘检查豁免；
生产必须使用独立物理磁盘。Nacos 服务端鉴权在本地也默认开启（`NACOS_AUTH_ENABLE=true`），
Redis 同样默认 `requirepass`；签名/互信密钥与口令仍仅适用于本地，生产必须全部覆盖。

### 3. 关键管理入口

| 用途                | 地址                                                             |
| ------------------- | ---------------------------------------------------------------- |
| Nacos 控制台        | http://localhost:8849/（Nacos 3 独立控制台；服务 API 仍为 8848） |
| Sentinel Dashboard  | http://localhost:8858（sentinel/sentinel）                       |
| RocketMQ Dashboard  | http://localhost:9878                                            |
| Seata（TC）事务端口 | localhost:8091（当前镜像不提供旧版 7091 控制台）                 |
| RustFS Console      | http://localhost:9001                                            |
| Elasticsearch       | http://localhost:9200                                            |
| Grafana             | http://localhost:3000（admin/admin123）                          |
| Prometheus          | http://localhost:9090                                            |
| Higress Console     | http://localhost:8001                                            |

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
业务账号统一 `admin`；**所有口令只存在于 `deploy/.env`**（模板 `deploy/.env.example` 里是
`change-me-*` 占位符），compose 与 `application.yml` 都不再保留明文口令默认值。
`deploy/.env` 已被 `.gitignore` 忽略，不要把填好真实口令的本文件提交进仓库。

漏配会 fail-fast 而不是静默连不上，两层各管一段：

- compose 层用 `${VAR:?}`：`REDIS_PASSWORD`、`NACOS_PASSWORD`、`MYSQL_ROOT_PASSWORD` 与 5 个
  `MYSQL_*_PASSWORD`、`JWT_SECRET`（只注入 auth-service）、`SENTINEL_AUTH_PASSWORD`、
  `GRAFANA_ADMIN_PASSWORD`、`DRUID_MONITOR_PASSWORD`（只注入开了 Druid 监控的 5 个服务）。
- Spring 层用无兜底的 `${VAR}`：各服务 `application.yml` 里的数据源口令、`spring.data.redis.password`、
  JetCache 的 `redis://:口令@…` 连接串、Druid `login-password`、`nabu.jwt.secret`。

裸机跑服务前先 `source deploy/scripts/dev-env.sh`，它把上述凭据（`*_PASSWORD` / `*_USERNAME` /
`*_ACCESS_KEY` / `*_SECRET`，含 `JWT_SECRET`）导出到当前 shell；漏了这一步服务会直接报
`Could not resolve placeholder`。`JWT_SECRET` 是 HMAC 密钥不是口令，必须用 >=512-bit 的随机值
（`openssl rand -base64 64`），仓库内已不留默认密钥。

两个账号是例外：

- **Canal 用独立的 `canal` 账号**（需要全局 binlog 权限，不能复用业务账号），口令另见
  `deploy/config/canal/instance.properties.example` 与 `deploy/config/mysql/init/README.md`。
- **Nacos 内置 `nacos` 账号**的口令存在容器内嵌 derby 里，改 `.env` 不会改到服务端，
  新环境必须按上面「首次开鉴权：Nacos 内置账号必须自己建」一节先建号。

## 说明

本仓库定位为工程实践参考，部分实现做了有意的简化（例如敏感词过滤、Canal 消息结构、
SchedulerX 本地替代方案），代码中都留有中文注释说明"生产环境应该怎么做"，方便按需扩展为
真实可用的实现。
