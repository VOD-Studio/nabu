package com.xfy.nabu.stat.dubbo;

import com.xfy.nabu.api.stat.dto.HotTopicDTO;
import com.xfy.nabu.api.stat.service.StatService;
import com.xfy.nabu.stat.service.StatBizService;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * Dubbo Provider：对外暴露 {@link StatService}，内部转发给 {@link StatBizService}。
 * 这样拆分是为了让业务逻辑不直接依赖 Dubbo 注解，方便未来暴露 REST 管理接口时复用同一套业务代码。
 */
@DubboService
public class StatServiceDubboImpl implements StatService {

    private final StatBizService statBizService;

    public StatServiceDubboImpl(StatBizService statBizService) {
        this.statBizService = statBizService;
    }

    @Override
    public void recordTopicView(Long topicId, Long viewerUserId) {
        statBizService.recordTopicView(topicId, viewerUserId);
    }

    @Override
    public long getTopicViewCount(Long topicId) {
        return statBizService.getTopicViewCount(topicId);
    }

    @Override
    public long getTopicUniqueVisitorCount(Long topicId) {
        return statBizService.getTopicUniqueVisitorCount(topicId);
    }

    @Override
    public List<HotTopicDTO> topHotTopics(int limit) {
        return statBizService.topHotTopics(limit);
    }
}
