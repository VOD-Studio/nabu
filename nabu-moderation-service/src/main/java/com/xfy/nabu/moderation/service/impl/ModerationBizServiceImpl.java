package com.xfy.nabu.moderation.service.impl;

import com.xfy.nabu.api.moderation.dto.ModerationResultDTO;
import com.xfy.nabu.moderation.config.ModerationProperties;
import com.xfy.nabu.moderation.domain.ModerationLogEntity;
import com.xfy.nabu.moderation.mapper.ModerationLogMapper;
import com.xfy.nabu.moderation.service.ModerationBizService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 极简敏感词过滤实现：命中 {@link ModerationProperties#getSensitiveWords()} 里的任意词即判 REJECT。
 * <b>这是占位实现</b>，仅演示"同步预检 + 异步复核 + 消费幂等落库"这条链路怎么串起来；
 * 生产环境应替换为第三方内容安全服务（如阿里云内容安全/数美等）或自建文本分类模型，
 * 敏感词检测也只是内容审核的一个子能力，真实系统还需要图片/视频审核、上下文语义判断等。
 */
@Service
public class ModerationBizServiceImpl implements ModerationBizService {

    private final ModerationProperties moderationProperties;
    private final ModerationLogMapper moderationLogMapper;

    public ModerationBizServiceImpl(ModerationProperties moderationProperties,
                                     ModerationLogMapper moderationLogMapper) {
        this.moderationProperties = moderationProperties;
        this.moderationLogMapper = moderationLogMapper;
    }

    @Override
    public ModerationResultDTO checkText(String content) {
        ModerationResultDTO result = new ModerationResultDTO();
        if (content == null || content.isBlank()) {
            result.setVerdict("PASS");
            result.setReason("空文本，跳过检测");
            return result;
        }
        List<String> sensitiveWords = moderationProperties.getSensitiveWords();
        for (String word : sensitiveWords) {
            if (content.contains(word)) {
                result.setVerdict("REJECT");
                result.setReason("命中敏感词: " + word);
                return result;
            }
        }
        result.setVerdict("PASS");
        result.setReason("未命中敏感词");
        return result;
    }

    @Override
    public void asyncReview(String targetType, Long targetId, String content) {
        ModerationResultDTO result = checkText(content);
        ModerationLogEntity entity = new ModerationLogEntity();
        entity.setTargetType(targetType);
        entity.setTargetId(targetId);
        entity.setVerdict(result.getVerdict());
        entity.setReason(result.getReason());
        // INSERT IGNORE 语义见 ModerationLogMapper.xml 注释：重复消费不会产生重复审核记录。
        moderationLogMapper.insertIgnore(entity);
    }
}
