package com.xfy.nabu.forum.service;

import com.xfy.nabu.api.forum.dto.CommentDTO;
import com.xfy.nabu.api.forum.dto.TopicDTO;
import com.xfy.nabu.common.result.PageResult;

/**
 * 内部业务服务接口（区别于对外的 Dubbo {@code ForumService}），供本模块内部（Dubbo 实现类 / 未来的 REST 管理接口）复用。
 */
public interface ForumBizService {

    TopicDTO getTopicById(Long topicId);

    PageResult<TopicDTO> listTopicsByBoard(Long boardId, long pageNum, long pageSize);

    Long createTopic(TopicDTO topicDTO);

    Long createComment(CommentDTO commentDTO);

    CommentDTO getCommentById(Long commentId);

    /** 使用 100 积分购买帖子置顶：演示 Seata AT 跨服务分布式事务 */
    void pinTopicWithPoints(Long topicId, Long operatorUserId);
}
