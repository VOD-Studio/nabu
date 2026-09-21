package com.xfy.nabu.admin.controller;

import com.xfy.nabu.api.stat.dto.HotTopicDTO;
import com.xfy.nabu.api.stat.service.StatService;
import com.xfy.nabu.common.result.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台管理端 - 数据看板示例接口。
 *
 * <p>本 Controller 只是骨架示例，展示后台管理服务如何通过 {@code @DubboReference}
 * 调用 {@link StatService} 汇总统计数据，未来可以在此基础上扩展更多看板指标。</p>
 */
@RestController
public class AdminDashboardController {

    @DubboReference
    private StatService statService;

    /**
     * 查询当前热门帖子榜单，供管理后台展示数据看板使用。
     */
    @GetMapping("/admin/dashboard/hot-topics")
    public Result<List<HotTopicDTO>> hotTopics() {
        List<HotTopicDTO> hotTopics = statService.topHotTopics(10);
        return Result.success(hotTopics);
    }
}
