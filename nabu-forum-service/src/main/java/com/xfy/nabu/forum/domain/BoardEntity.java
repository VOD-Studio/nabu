package com.xfy.nabu.forum.domain;

import com.xfy.nabu.common.base.BaseEntity;

/**
 * 版块实体，对应表 t_board。
 */
public class BoardEntity extends BaseEntity {

    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
