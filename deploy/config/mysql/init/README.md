# MySQL 初始化说明

各服务的表结构由各自模块内的 Flyway 迁移脚本管理
（路径：`nabu-<service>/src/main/resources/db/migration/V*__*.sql`），
应用启动时会自动执行，无需在这里手工建表。

本目录预留给"不属于任何单个服务、需要在容器启动时提前跑一次"的初始化脚本，
例如 Canal 同步账号的 `GRANT REPLICATION SLAVE ...`。当前暂未启用真实 Canal 同步，
如需启用，可以在这里新增 `01-canal-user.sql`，内容类似：

```sql
-- 把 <CANAL_PASSWORD> 换成与 deploy/.env 里 MYSQL_FORUM_PASSWORD 一致的值，
-- 并同步改 deploy/config/canal/instance.properties 的 canal.instance.dbPassword；
-- 三处必须一致，仓库内的跟踪文件不写实际口令。
CREATE USER IF NOT EXISTS 'canal'@'%' IDENTIFIED BY '<CANAL_PASSWORD>';
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
FLUSH PRIVILEGES;
```

并在 `compose.infrastructure.yml` 里给 `mysql-forum` 服务追加：

```yaml
    volumes:
      - ./config/mysql/init:/docker-entrypoint-initdb.d
    command: >
      --character-set-server=utf8mb4 --collation-server=utf8mb4_general_ci
      --default-authentication-plugin=mysql_native_password
      --log-bin=mysql-bin --binlog-format=ROW --server-id=1
```
