package com.xfy.nabu.forum.mapper;

import com.xfy.nabu.forum.domain.TopicEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子 Mapper。
 */
@Mapper
public interface TopicMapper {

    TopicEntity selectById(@Param("id") Long id);

    /**
     * 分页查询某版块下的帖子：pinned=1 的置顶帖排在最前，其余按 created_at desc 排列。
     */
    List<TopicEntity> listByBoard(@Param("boardId") Long boardId,
                                   @Param("offset") long offset,
                                   @Param("limit") long limit);

    long countByBoard(@Param("boardId") Long boardId);

    int insert(TopicEntity entity);

    /**
     * 评论数 +1，供 createComment 更新聚合统计使用。
     */
    int incrementCommentCount(@Param("id") Long id);

    /**
     * 置顶：pinned = 1。供 pinTopicWithPoints 的 Seata 全局事务分支使用。
     */
    int updatePinned(@Param("id") Long id, @Param("pinned") Integer pinned);
}
