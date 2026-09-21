-- =====================================================================================
-- Seata AT 模式回滚日志表 undo_log（nabu_user 库）
--
-- user-service 是"购买帖子置顶扣积分"全局事务的第二个分支（RM）：
-- forum-service（TM）发起 @GlobalTransactional 后，通过 Dubbo 调用本服务的 changePoints；
-- Seata 客户端会在本地事务提交前把积分变更的前后镜像写入本库的 undo_log，
-- 全局事务一旦决定回滚，本服务凭 xid + branch_id 取回镜像，生成反向 SQL 把积分加回去。
-- 表结构必须与 forum-service 侧完全一致（同名同结构），列名为 Seata 硬编码，不要改名。
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
    UNIQUE KEY ux_undo_log (xid, branch_id),
    KEY ix_log_created (log_created)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'Seata AT 模式事务回滚日志表';
