package com.xfy.nabu.web.controller;

import com.xfy.nabu.api.forum.dto.CommentDTO;
import com.xfy.nabu.api.forum.dto.TopicDTO;
import com.xfy.nabu.api.forum.service.ForumService;
import com.xfy.nabu.api.stat.service.StatService;
import com.xfy.nabu.common.result.PageQuery;
import com.xfy.nabu.common.result.PageResult;
import com.xfy.nabu.common.result.Result;
import com.xfy.nabu.common.util.TraceContext;
import com.xfy.nabu.web.dto.CreateCommentRequest;
import com.xfy.nabu.web.dto.CreateTopicRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 版块 / 帖子 / 评论接口。均为薄转发层，调用 {@link ForumService} 完成实际业务逻辑，
 * 创建帖子/评论时的作者 id 取自 {@link TraceContext#getUserId()}（由 JWT 拦截器写入）。
 */
@RestController
public class ForumController {

    private static final Logger log = LoggerFactory.getLogger(ForumController.class);

    @DubboReference
    private ForumService forumService;

    @DubboReference
    private StatService statService;

    /**
     * 分页查询版块下的帖子列表。
     */
    @GetMapping("/api/v1/boards/{boardId}/topics")
    public Result<PageResult<TopicDTO>> listTopicsByBoard(@PathVariable("boardId") Long boardId, PageQuery pageQuery) {
        PageResult<TopicDTO> pageResult = forumService.listTopicsByBoard(boardId, pageQuery.getPageNum(), pageQuery.getPageSize());
        return Result.success(pageResult);
    }

    /**
     * 查询帖子详情。
     *
     * <p>同时通过 {@link StatService#recordTopicView(Long, Long)} 记录一次浏览量，
     * 该调用失败不应影响主流程（帖子详情仍应正常返回），因此用 try/catch 包裹并只打日志，不抛异常。</p>
     */
    @GetMapping("/api/v1/topics/{id}")
    public Result<TopicDTO> getTopicById(@PathVariable("id") Long id) {
        TopicDTO topicDTO = forumService.getTopicById(id);
        try {
            statService.recordTopicView(id, TraceContext.getUserId());
        } catch (Exception ex) {
            log.warn("记录帖子浏览量失败，topicId={}", id, ex);
        }
        return Result.success(topicDTO);
    }

    /**
     * 创建帖子，作者 id 取自当前登录用户。
     */
    @PostMapping("/api/v1/topics")
    public Result<Long> createTopic(@RequestBody CreateTopicRequest request) {
        TopicDTO topicDTO = new TopicDTO();
        topicDTO.setBoardId(request.getBoardId());
        topicDTO.setTitle(request.getTitle());
        topicDTO.setContent(request.getContent());
        topicDTO.setAuthorId(TraceContext.getUserId());
        Long topicId = forumService.createTopic(topicDTO);
        return Result.success(topicId);
    }

    /**
     * 创建评论，作者 id 取自当前登录用户。
     */
    @PostMapping("/api/v1/topics/{id}/comments")
    public Result<Long> createComment(@PathVariable("id") Long id, @RequestBody CreateCommentRequest request) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setTopicId(id);
        commentDTO.setReplyToId(request.getReplyToId());
        commentDTO.setContent(request.getContent());
        commentDTO.setAuthorId(TraceContext.getUserId());
        Long commentId = forumService.createComment(commentDTO);
        return Result.success(commentId);
    }

    /**
     * 购买置顶：使用积分购买帖子置顶，跨服务分布式事务由 forum-service 内部发起。
     */
    @PostMapping("/api/v1/topics/{id}/pin")
    public Result<Void> pinTopic(@PathVariable("id") Long id) {
        forumService.pinTopicWithPoints(id, TraceContext.getUserId());
        return Result.success();
    }
}
