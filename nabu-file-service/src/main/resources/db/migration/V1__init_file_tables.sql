CREATE TABLE IF NOT EXISTS t_file_meta (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uploader_id     BIGINT UNSIGNED NOT NULL COMMENT '上传者用户id',
    bucket          VARCHAR(64)   NOT NULL COMMENT 'RustFS bucket',
    object_key      VARCHAR(512)  NOT NULL COMMENT 'RustFS 对象 key',
    original_name   VARCHAR(255)  NOT NULL COMMENT '原始文件名',
    content_type    VARCHAR(128)           COMMENT 'MIME 类型',
    size_bytes      BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小(字节)',
    url             VARCHAR(1024)          COMMENT '可访问 URL',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    KEY idx_uploader (uploader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件元数据表';
