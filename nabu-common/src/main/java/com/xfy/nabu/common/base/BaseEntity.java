package com.xfy.nabu.common.base;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据库实体通用基类：id / 创建时间 / 更新时间 / 逻辑删除标记。
 * 各服务的 domain 实体建议继承该类，配合 MyBatis 通用字段自动填充（可在具体服务里接入 MetaObjectHandler 风格的拦截器）。
 */
public class BaseEntity implements Serializable {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** 逻辑删除：0-正常，1-已删除 */
    private Integer deleted = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
