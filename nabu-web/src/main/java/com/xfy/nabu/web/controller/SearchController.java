package com.xfy.nabu.web.controller;

import com.xfy.nabu.api.search.dto.TopicSearchDocDTO;
import com.xfy.nabu.api.search.service.SearchService;
import com.xfy.nabu.common.result.PageQuery;
import com.xfy.nabu.common.result.PageResult;
import com.xfy.nabu.common.result.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 搜索接口。薄转发层，调用 {@link SearchService} 完成实际业务逻辑。
 */
@RestController
public class SearchController {

    @DubboReference
    private SearchService searchService;

    /**
     * 按关键词分页搜索帖子。
     */
    @GetMapping("/api/v1/search/topics")
    public Result<PageResult<TopicSearchDocDTO>> searchTopics(
            @RequestParam("keyword") String keyword, PageQuery pageQuery) {
        PageResult<TopicSearchDocDTO> pageResult =
                searchService.searchTopics(keyword, pageQuery.getPageNum(), pageQuery.getPageSize());
        return Result.success(pageResult);
    }
}
