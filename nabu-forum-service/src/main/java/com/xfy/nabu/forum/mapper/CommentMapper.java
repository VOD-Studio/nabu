package com.xfy.nabu.forum.mapper;

import com.xfy.nabu.forum.domain.CommentEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 评论 Mapper。
 */
@Mapper
public interface CommentMapper {

    CommentEntity selectById(@Param("id") Long id);

    List<CommentEntity> listByTopic(
            @Param("topicId") Long topicId, @Param("offset") long offset, @Param("limit") long limit);

    int insert(CommentEntity entity);
}
