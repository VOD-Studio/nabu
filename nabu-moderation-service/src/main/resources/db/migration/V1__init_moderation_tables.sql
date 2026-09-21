CREATE TABLE IF NOT EXISTS t_moderation_log (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    target_type   VARCHAR(16)  NOT NULL COMMENT '审核对象类型：TOPIC/COMMENT',
    target_id     BIGINT UNSIGNED NOT NULL COMMENT '审核对象id',
    verdict       VARCHAR(16)  NOT NULL COMMENT '审核结果：PASS/REJECT/REVIEW',
    reason        VARCHAR(255)          COMMENT '命中原因，例如命中的敏感词',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    -- 唯一索引配合 insertIgnore 实现消费幂等：同一 (target_type,target_id) 重复消费不会产生重复审核记录
    UNIQUE KEY uk_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容审核记录表';
