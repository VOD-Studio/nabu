CREATE TABLE IF NOT EXISTS t_user (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    password_hash   VARCHAR(128) NOT NULL COMMENT '密码哈希(BCrypt)',
    nickname        VARCHAR(64)  NOT NULL COMMENT '昵称',
    avatar          VARCHAR(255)          COMMENT '头像 URL（RustFS）',
    level           INT NOT NULL DEFAULT 1 COMMENT '用户等级',
    points          INT NOT NULL DEFAULT 0 COMMENT '积分余额',
    status          TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-禁用',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
