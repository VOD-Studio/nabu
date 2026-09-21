package com.xfy.nabu.moderation.domain;

import com.xfy.nabu.common.base.BaseEntity;

/**
 * 审核记录：一条 (targetType, targetId) 记录对应"某帖子/某条评论"的一次异步复核结果。
 */
public class ModerationLogEntity extends BaseEntity {

    /** 审核对象类型：TOPIC / COMMENT */
    private String targetType;
    private Long targetId;
    /** 审核结果：PASS / REJECT / REVIEW */
    private String verdict;
    private String reason;

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
