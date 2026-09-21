package com.xfy.nabu.api.moderation.dto;

import java.io.Serializable;

public class ModerationResultDTO implements Serializable {

    /** 结果：PASS / REJECT / REVIEW（转人工审核） */
    private String verdict;
    private String reason;

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
