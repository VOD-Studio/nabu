package com.xfy.nabu.forum.dubbo;

import com.xfy.nabu.api.forum.dto.CommentDTO;
import com.xfy.nabu.api.forum.dto.TopicDTO;
import com.xfy.nabu.api.forum.service.ForumService;
import com.xfy.nabu.common.result.PageResult;
import com.xfy.nabu.forum.service.ForumBizService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * Dubbo Provider：对外暴露 {@link ForumService}，内部转发给 {@link ForumBizService}。
 * 这样拆分是为了让业务逻辑不直接依赖 Dubbo 注解，方便未来暴露 REST 管理接口时复用同一套业务代码。
 */
@DubboService
public class ForumServiceDubboImpl implements ForumService {

    private final ForumBizService forumBizService;

    public ForumServiceDubboImpl(ForumBizService forumBizService) {
        this.forumBizService = forumBizService;
    }

    @Override
    public TopicDTO getTopicById(Long topicId) {
        return forumBizService.getTopicById(topicId);
    }

    @Override
    public PageResult<TopicDTO> listTopicsByBoard(Long boardId, long pageNum, long pageSize) {
        return forumBizService.listTopicsByBoard(boardId, pageNum, pageSize);
    }

    @Override
    public Long createTopic(TopicDTO topicDTO) {
        return forumBizService.createTopic(topicDTO);
    }

    @Override
    public Long createComment(CommentDTO commentDTO) {
        return forumBizService.createComment(commentDTO);
    }

    @Override
    public CommentDTO getCommentById(Long commentId) {
        return forumBizService.getCommentById(commentId);
    }

    @Override
    public void pinTopicWithPoints(Long topicId, Long operatorUserId) {
        forumBizService.pinTopicWithPoints(topicId, operatorUserId);
    }
}
