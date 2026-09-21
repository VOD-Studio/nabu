package com.xfy.nabu.search.dubbo;

import com.xfy.nabu.api.search.dto.TopicSearchDocDTO;
import com.xfy.nabu.api.search.service.SearchService;
import com.xfy.nabu.common.result.PageResult;
import com.xfy.nabu.search.service.SearchBizService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * Dubbo Provider：对外暴露 {@link SearchService}，内部转发给 {@link SearchBizService}。
 * 这样拆分是为了让业务逻辑不直接依赖 Dubbo 注解，方便未来暴露 REST 管理接口时复用同一套业务代码。
 */
@DubboService
public class SearchServiceDubboImpl implements SearchService {

    private final SearchBizService searchBizService;

    public SearchServiceDubboImpl(SearchBizService searchBizService) {
        this.searchBizService = searchBizService;
    }

    @Override
    public PageResult<TopicSearchDocDTO> searchTopics(String keyword, long pageNum, long pageSize) {
        return searchBizService.searchTopics(keyword, pageNum, pageSize);
    }
}
