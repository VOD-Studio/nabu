-- =====================================================================================
-- Seata AT 模式回滚日志表 undo_log（nabu_forum 库）
--
-- 为什么必须建这张表：
-- AT 模式是"无侵入"的补偿型事务——RM（资源管理器）在业务 SQL 执行前后，会自动解析 SQL 并把
-- 数据改动前/后的镜像（beforeImage / afterImage）序列化后写入 undo_log，同时把本次分支事务的
-- xid + branch_id 注册到 TC（事务协调器）。全局事务一旦决定回滚，TC 通知各分支回滚，RM 再按
-- xid + branch_id 从 undo_log 里取出镜像，反向生成补偿 SQL 把数据还原。
-- 因此：任何一个参与 AT 全局事务的业务库都必须有这张 undo_log 表，否则分支提交时就会报
-- "Table 'xxx.undo_log' doesn't exist"，"购买帖子置顶扣积分"（ForumBizServiceImpl#pinTopicWithPoints）
-- 的第 1 步本地更新将无从回滚。
--
-- 表名、列名均为 Seata 客户端硬编码（默认值见 org.apache.seata...UndoProperties#logTable = "undo_log"，
-- MySQL 方言的 INSERT 语句使用 log_created / log_modified 两列并由 SQL 侧 now(6) 填充），
-- 不要重命名、不要改成 created_at/updated_at，也不要用本项目的 BaseEntity 逻辑删除字段去"统一风格"。
-- 列定义与 Seata 2.5.0 官方 MySQL 初始化脚本（script/server/db/mysql.sql）保持一致。
--
-- 注意：Seata Server 侧本次部署用的是 STORE_MODE=file（见 deploy/compose.middleware.yml），
-- 所以这里只需要业务库的 undo_log，不需要 global_table / branch_table / lock_table；
-- 若将来把 TC 切换为 db 存储模式，再为 Seata Server 单独准备那三张表。
-- =====================================================================================

CREATE TABLE IF NOT EXISTS undo_log (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    branch_id     BIGINT UNSIGNED NOT NULL COMMENT '分支事务 id',
    xid           VARCHAR(128) NOT NULL COMMENT '全局事务 id（由 TM 侧 @GlobalTransactional 开启时生成并随 Dubbo 调用传播）',
    context       VARCHAR(128) NOT NULL COMMENT '回滚信息序列化方式，如 jackson',
    rollback_info LONGBLOB     NOT NULL COMMENT 'beforeImage/afterImage 等回滚快照数据',
    log_status    INT NOT NULL COMMENT '0-正常状态（事务提交中待删除），1-防御状态（全局事务已完成时的空回滚记录）',
    log_created   DATETIME(6) NOT NULL COMMENT '创建时间，Seata 客户端用 now(6) 写入，过期记录由后台清理任务按此列删除',
    log_modified  DATETIME(6) NOT NULL COMMENT '修改时间',
    ext           VARCHAR(100) DEFAULT NULL COMMENT '扩展字段（官方 2.x 脚本保留列，当前 MySQL 方言不写入）',
    PRIMARY KEY (id),
    -- 回滚路径按 xid + branch_id 精确定位 UndoLog，必须唯一：既是查询索引，也防止重复写入同一条回滚记录
    UNIQUE KEY ux_undo_log (xid, branch_id),
    -- 清理历史 undo_log（DELETE FROM undo_log WHERE log_created < ?）走该索引，避免全表扫描
    KEY ix_log_created (log_created)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'Seata AT 模式事务回滚日志表';
