CREATE TABLE IF NOT EXISTS t_notification (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    receiver_id     BIGINT UNSIGNED NOT NULL COMMENT '通知接收人 id',
    type            VARCHAR(32)  NOT NULL COMMENT '通知类型：COMMENT_REPLY/LIKE/MENTION/SYSTEM/FOLLOW',
    content         VARCHAR(500) NOT NULL COMMENT '通知文案',
    link_url        VARCHAR(255)          COMMENT '跳转链接',
    is_read         TINYINT NOT NULL DEFAULT 0 COMMENT '0-未读 1-已读',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    KEY idx_receiver_created (receiver_id, created_at),
    KEY idx_receiver_unread (receiver_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';
