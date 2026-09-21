# Canal 同步链路：从 MySQL binlog 到 Elasticsearch

## 目标链路

```text
nabu-forum-service
      │  写帖子
      ▼
  mysql-forum (nabu_forum.t_topic)
      │  binlog(ROW)
      ▼
  canal-server  ── 订阅 t_topic 变更
      │
      ▼
  RocketMQ topic: CanalBinlogSyncEvent
      │
      ▼
  nabu-search-service (CanalBinlogSyncEventListener)
      │
      ▼
  Elasticsearch (nabu_topic_index)
```

`nabu-forum-service` 的业务代码**不会**同步写 Elasticsearch——索引完全由这条
binlog → MQ → 消费落库 的链路异步驱动。这样做的意义：业务写入路径不需要关心搜索
索引是否可用（搜索服务挂了，发帖依然成功），搜索索引最终一致而不是强一致。

## 当前仓库里的状态

- `deploy/compose.middleware.yml` 里的 `canal-server` 服务当前是**空跑**（没有挂载 instance 配置），
  启动后不会真正监听任何库表，只是为了让 `docker compose up` 时这个容器存在、
  不影响其它服务启动，方便你按下面步骤逐步接入。
- `nabu-search-service` 里的 `CanalBinlogSyncEvent` / `CanalBinlogSyncEventListener`
  消费的是一个**简化后的自定义 JSON 结构**，不是 Canal 官方协议（`canal.protocol.CanalEntry`），
  类注释里已经说明差异；这是刻意做的取舍，方便你在不装 Canal Adapter 的情况下，
  用任意方式（比如手写一个轮询 binlog 的小程序，甚至手动 `Producer` 发一条测试消息）
  就能把消息投进这个 topic 验证消费逻辑跑通。

## 要让 Canal 真正跑起来，需要做的事

1. **给 `mysql-forum` 开启 ROW 格式 binlog**：见 `deploy/config/mysql/init/README.md`。
2. **创建 Canal 同步账号**：同样见上面的 README，创建 `canal` 用户并授权
   `SELECT, REPLICATION SLAVE, REPLICATION CLIENT`。
3. **配置 Canal instance**：参考本仓库已有的模板
   `deploy/config/canal/instance.properties.example`，填入真实的 MySQL 地址
   （`canal.instance.master.address=mysql-forum:3306`）、账号密码、
   要过滤的库表（`nabu_forum.t_topic`）、投递的目标 MQ topic（`CanalBinlogSyncEvent`）。
4. **把配置挂载进容器**：修改 `deploy/compose.middleware.yml` 的 `canal-server` 服务，
   加一段 `volumes`，把 `deploy/config/canal/instance.properties.example`
   （重命名去掉 `.example`）挂载到 Canal 容器内
   `conf/canal-instance/instance.properties` 对应的路径（具体路径以你拉取的
   `canal/canal-server` 镜像版本为准，可先 `docker exec -it nabu-canal-server bash`
   进容器确认目录结构，再决定挂载点，不要凭记忆猜路径）。
5. **确认 Canal Server 的 MQ 投递格式**：Canal 官方 `canal.adapter` 投递到 RocketMQ
   的消息格式（flat 或自定义 protobuf）与上面第 3 点配置的 `canal.mq.flatMessage`
   等相关；如果用的是 `canal.mq.flatMessage=true`（简单 JSON 行格式），消息结构和
   本仓库 `CanalBinlogSyncEvent` 假设的 `{type, table, data}` 结构并不完全一致，
   需要按 Canal 实际输出的字段名（`database`/`table`/`type`/`data`，其中
   `type` 是 `INSERT`/`UPDATE`/`DELETE`，`data` 是行数据数组而非单对象）调整
   `nabu-search-service` 的 `CanalBinlogSyncEventListener` 解析逻辑，
   或者按 Canal 官方文档要求引入 `canal.client`/`canal.protocol` 依赖，用
   `CanalEntry.Entry`/`CanalEntry.RowChange` 反序列化，替换掉当前手写的简化模型。
6. **端到端验证顺序**：
   ```bash
   # 1) 起基础设施 + 中间件
   deploy/scripts/up.sh middleware
   # 2) 起应用（或者本地跑 search-service 单独调试）
   deploy/scripts/up.sh all
   # 3) 用 nabu-web 暴露的接口发一个帖子（或本地直接调 forum-service 的 Dubbo 接口）
   # 4) 观察 canal-server 日志：docker logs nabu-canal-server
   # 5) 观察 search-service 是否消费到消息并写入 ES：
   curl "http://localhost:9200/nabu_topic_index/_search?q=*&pretty"
   ```

## 更省事的替代方案（如果只是想验证"搜索能查到刚发的帖子"）

Canal 这一整套部署 + 配置本身比较重，这套设计的核心价值主要在"理解 CDC 思想"，而不是"必须亲手
配好 Canal"。如果只是想先跑通发帖→可搜索的闭环，可以：

- 暂时不改业务代码，写一个小的 `@Scheduled` 任务（可以放在 `nabu-task-service`），
  每几秒轮询 `t_topic` 里 `updated_at` 比上次记录时间新的行，手动投一条
  `CanalBinlogSyncEvent`（按本仓库已约定的简化 JSON 格式）到 RocketMQ；
- 或者直接跳过 Canal/MQ 这一段，在 `nabu-forum-service` 发帖成功的
  `createTopic` 事务提交后，同步调用一次 `nabu-search-service` 的 Dubbo 接口
  写索引（牺牲"最终一致、解耦"这个设计目标，换取最快看到效果）。

等这两种简化路径能跑通，再切回真正的 Canal 链路，对比"业务代码是否要改动"，
你会更直观地理解为什么生产系统更倾向 CDC 而不是同步双写。
