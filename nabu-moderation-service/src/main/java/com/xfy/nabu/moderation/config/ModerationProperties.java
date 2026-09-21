package com.xfy.nabu.moderation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 审核相关配置，对应 application.yml 的 nabu.moderation.* 前缀。
 * 默认给一组极简敏感词，仅用于演示"命中即拒绝"的最简规则；生产环境应替换为
 * 第三方内容安全 API 或自建模型，而不是靠这份静态词表。
 */
@Component
@ConfigurationProperties(prefix = "nabu.moderation")
public class ModerationProperties {

    private List<String> sensitiveWords = new ArrayList<>(List.of("广告", "诈骗", "赌博"));

    public List<String> getSensitiveWords() {
        return sensitiveWords;
    }

    public void setSensitiveWords(List<String> sensitiveWords) {
        this.sensitiveWords = sensitiveWords;
    }
}
