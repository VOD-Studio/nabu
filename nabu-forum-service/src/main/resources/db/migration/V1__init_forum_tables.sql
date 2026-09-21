CREATE TABLE IF NOT EXISTS t_board (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(64)  NOT NULL COMMENT '版块名称',
    description     VARCHAR(255)          COMMENT '版块描述',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='版块表';

CREATE TABLE IF NOT EXISTS t_topic (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    board_id        BIGINT UNSIGNED NOT NULL COMMENT '所属版块 id',
    author_id       BIGINT UNSIGNED NOT NULL COMMENT '作者 user id（跨库，仅存 id，不建外键）',
    title           VARCHAR(128) NOT NULL COMMENT '标题',
    content         TEXT         NOT NULL COMMENT '正文内容',
    view_count      INT NOT NULL DEFAULT 0 COMMENT '浏览数',
    comment_count   INT NOT NULL DEFAULT 0 COMMENT '评论数',
    like_count      INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    pinned          TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
    status          TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-审核中 2-已屏蔽',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    KEY idx_board_pinned_created (board_id, pinned, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

CREATE TABLE IF NOT EXISTS t_comment (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    topic_id        BIGINT UNSIGNED NOT NULL COMMENT '所属帖子 id',
    author_id       BIGINT UNSIGNED NOT NULL COMMENT '作者 user id（跨库，仅存 id，不建外键）',
    reply_to_id     BIGINT UNSIGNED          COMMENT '被回复的评论 id，楼层评论为 NULL',
    content         TEXT         NOT NULL COMMENT '评论内容',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    KEY idx_topic_created (topic_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
