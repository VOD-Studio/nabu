package com.xfy.nabu.moderation.mapper;

import com.xfy.nabu.moderation.domain.ModerationLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModerationLogMapper {

    /**
     * INSERT IGNORE 落审核记录：依赖 t_moderation_log 表上 (target_type, target_id) 的唯一索引，
     * 重复消费同一事件时不会产生重复记录（消费幂等的简单实现）。
     */
    int insertIgnore(ModerationLogEntity entity);
}
