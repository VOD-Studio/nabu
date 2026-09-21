package com.xfy.nabu.forum.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.xfy.nabu.api.forum.dto.CommentDTO;
import com.xfy.nabu.api.forum.dto.TopicDTO;
import com.xfy.nabu.api.moderation.dto.ModerationResultDTO;
import com.xfy.nabu.api.moderation.service.ModerationService;
import com.xfy.nabu.api.user.dto.PointsChangeDTO;
import com.xfy.nabu.api.user.service.UserService;
import com.xfy.nabu.common.exception.BusinessException;
import com.xfy.nabu.common.result.PageResult;
import com.xfy.nabu.common.result.ResultCode;
import com.xfy.nabu.forum.domain.CommentEntity;
import com.xfy.nabu.forum.domain.TopicEntity;
import com.xfy.nabu.forum.mapper.CommentMapper;
import com.xfy.nabu.forum.mapper.TopicMapper;
import com.xfy.nabu.forum.mq.CommentCreatedEvent;
import com.xfy.nabu.forum.mq.TopicCreatedEvent;
import com.xfy.nabu.forum.service.ForumBizService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 论坛业务实现：版块/帖子/评论的核心逻辑。
 * <p>
 * 关于 {@code @GlobalTransactional} 的包名选择：Seata 从孵化器毕业后 Maven groupId 已迁移为
 * {@code org.apache.seata}（本项目 pom.xml 中 seata-spring-boot-starter 使用的正是该 groupId），
 * 与之配套的 2.x 系列源码（见 apache/incubator-seata 仓库 {@code integration-tx-api} 模块）
 * 也已将注解类同步迁移到 {@code org.apache.seata.spring.annotation.GlobalTransactional}；
 * 旧的 {@code io.seata.spring.annotation.GlobalTransactional} 仅保留在专门的 {@code compatible}
 * 兼容模块中，供历史项目升级过渡使用。本项目全新搭建、且 groupId 已统一为 org.apache.seata，
 * 因此这里直接使用新包名 {@code org.apache.seata.spring.annotation.GlobalTransactional}，
 * 避免新旧包混用造成的类冲突风险。
 */
@Service
public class ForumBizServiceImpl implements ForumBizService {

    private final TopicMapper topicMapper;
    private final CommentMapper commentMapper;
    private final RocketMQTemplate rocketMQTemplate;

    /** 内容审核服务：createComment 做同步预检（快速拦截明显违规内容） */
    @DubboReference
    private ModerationService moderationService;

    /** 用户服务：pinTopicWithPoints 跨服务扣积分，与本服务共同参与 Seata 全局事务 */
    @DubboReference
    private UserService userService;

    public ForumBizServiceImpl(TopicMapper topicMapper, CommentMapper commentMapper, RocketMQTemplate rocketMQTemplate) {
        this.topicMapper = topicMapper;
        this.commentMapper = commentMapper;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    @Cached(name = "forum:topic:", key = "#topicId", expire = 300, cacheType = CacheType.BOTH)
    public TopicDTO getTopicById(Long topicId) {
        TopicEntity entity = topicMapper.selectById(topicId);
        return entity == null ? null : toDTO(entity);
    }

    @Override
    public PageResult<TopicDTO> listTopicsByBoard(Long boardId, long pageNum, long pageSize) {
        long offset = (pageNum - 1) * pageSize;
        List<TopicEntity> entities = topicMapper.listByBoard(boardId, offset, pageSize);
        long total = topicMapper.countByBoard(boardId);
        if (entities == null || entities.isEmpty()) {
            return PageResult.empty(pageNum, pageSize);
        }
        List<TopicDTO> records = entities.stream().map(this::toDTO).collect(Collectors.toList());
        return new PageResult<>(records, total, pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTopic(TopicDTO topicDTO) {
        TopicEntity entity = new TopicEntity();
        entity.setBoardId(topicDTO.getBoardId());
        entity.setAuthorId(topicDTO.getAuthorId());
        entity.setTitle(topicDTO.getTitle());
        entity.setContent(topicDTO.getContent());
        topicMapper.insert(entity);

        // 发帖成功后异步发布 TopicCreatedEvent，供审核/搜索/统计服务消费，避免同步调用拖慢主流程。
        TopicCreatedEvent event = new TopicCreatedEvent(entity.getId(), entity.getBoardId(), entity.getAuthorId(), entity.getTitle(), entity.getContent());
        rocketMQTemplate.syncSend(TopicCreatedEvent.TOPIC, MessageBuilder.withPayload(JSON.toJSONString(event)).build());

        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComment(CommentDTO commentDTO) {
        // 同步预检：调用审核服务做敏感词等快速拦截。REJECT 直接拒绝创建；
        // REVIEW（疑似违规，需转人工复核）/ PASS 都允许创建，异步复核由 moderation-service
        // 消费下面发布的 CommentCreatedEvent 完成，此处仅做快速拦截，不阻塞正常发帖体验。
        ModerationResultDTO moderationResult = moderationService.checkText(commentDTO.getContent());
        if (moderationResult != null && "REJECT".equals(moderationResult.getVerdict())) {
            throw new BusinessException(ResultCode.PARAM_INVALID.getCode(),
                    "评论内容审核不通过：" + moderationResult.getReason());
        }

        CommentEntity entity = new CommentEntity();
        entity.setTopicId(commentDTO.getTopicId());
        entity.setAuthorId(commentDTO.getAuthorId());
        entity.setReplyToId(commentDTO.getReplyToId());
        entity.setContent(commentDTO.getContent());
        commentMapper.insert(entity);
        topicMapper.incrementCommentCount(commentDTO.getTopicId());

        // 发布 CommentCreatedEvent，供 moderation-service 异步复核、notify-service 发送 @回复通知等。
        CommentCreatedEvent event = new CommentCreatedEvent(entity.getId(), entity.getTopicId(), entity.getAuthorId(), entity.getReplyToId(), entity.getContent());
        rocketMQTemplate.syncSend(CommentCreatedEvent.TOPIC, MessageBuilder.withPayload(JSON.toJSONString(event)).build());

        return entity.getId();
    }

    /**
     * "购买帖子置顶"：本方法演示 Seata AT 模式跨服务分布式事务。
     * <p>
     * 步骤：1) 本地更新 t_topic.pinned=1；2) 通过 Dubbo 调用 user-service 的 changePoints
     * 扣减操作人 100 积分。两步分别落在 nabu_forum、nabu_user 两个独立数据库上，
     * 属于典型的跨服务（跨数据源）事务场景：如果第 2 步（扣积分）因积分不足等原因失败，
     * Seata 的事务协调器（TC）会通知已经提交的第 1 步分支事务（本地更新 pinned）自动回滚，
     * 保证 "扣积分成功" 与 "置顶成功" 要么同时发生、要么都不发生，避免出现
     * "扣了积分却没置顶成功" 或 "置顶成功却没扣到积分" 的数据不一致。
     * <p>
     * {@code @GlobalTransactional} 由本方法（事务发起方 TM）开启全局事务；
     * user-service 侧的 {@code changePoints} 方法通过 Dubbo Filter 自动加入同一个全局事务分支（RM），
     * 无需在 user-service 侧再显式标注 {@code @GlobalTransactional}。
     */
    @Override
    @GlobalTransactional(name = "pin-topic-with-points", rollbackFor = Exception.class)
    public void pinTopicWithPoints(Long topicId, Long operatorUserId) {
        int affected = topicMapper.updatePinned(topicId, 1);
        if (affected == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }

        PointsChangeDTO changeDTO = new PointsChangeDTO();
        changeDTO.setUserId(operatorUserId);
        changeDTO.setDelta(-100);
        changeDTO.setReason("购买帖子置顶");
        changeDTO.setIdempotentKey("topic:pin:" + topicId);
        userService.changePoints(changeDTO);
    }

    @Override
    public CommentDTO getCommentById(Long commentId) {
        CommentEntity entity = commentMapper.selectById(commentId);
        return entity == null ? null : toCommentDTO(entity);
    }

    private CommentDTO toCommentDTO(CommentEntity entity) {
        CommentDTO dto = new CommentDTO();
        dto.setId(entity.getId());
        dto.setTopicId(entity.getTopicId());
        dto.setAuthorId(entity.getAuthorId());
        dto.setReplyToId(entity.getReplyToId());
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    private TopicDTO toDTO(TopicEntity entity) {
        TopicDTO dto = new TopicDTO();
        dto.setId(entity.getId());
        dto.setBoardId(entity.getBoardId());
        dto.setAuthorId(entity.getAuthorId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setViewCount(entity.getViewCount());
        dto.setCommentCount(entity.getCommentCount());
        dto.setLikeCount(entity.getLikeCount());
        dto.setPinned(entity.getPinned());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
