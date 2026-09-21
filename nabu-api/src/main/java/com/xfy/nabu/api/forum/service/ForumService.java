package com.xfy.nabu.api.forum.service;

import com.xfy.nabu.api.forum.dto.CommentDTO;
import com.xfy.nabu.api.forum.dto.TopicDTO;
import com.xfy.nabu.common.result.PageResult;

/**
 * 版块/帖子/评论服务 Dubbo 接口，由 nabu-forum-service 提供实现。
 * 帖子置顶等需要跨服务扣积分的场景，由 forum-service 内部发起
 * {@code @GlobalTransactional}，通过 Dubbo 调用 user-service 的 changePoints。
 */
public interface ForumService {

    TopicDTO getTopicById(Long topicId);

    PageResult<TopicDTO> listTopicsByBoard(Long boardId, long pageNum, long pageSize);

    Long createTopic(TopicDTO topicDTO);

    Long createComment(CommentDTO commentDTO);

    /**
     * 按评论 id 查询评论详情（含内容），供 nabu-moderation-service 等下游服务在异步复核时
     * 取回 CommentCreatedEvent 里未携带的正文内容（事件体只带 id 级轻量字段，正文按需回查，
     * 避免 MQ 消息体过大）。不存在时返回 null。
     */
    CommentDTO getCommentById(Long commentId);

    /** 使用 100 积分购买帖子置顶：演示 Seata AT 跨服务分布式事务 */
    void pinTopicWithPoints(Long topicId, Long operatorUserId);
}
