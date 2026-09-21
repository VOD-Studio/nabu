package com.xfy.nabu.api.stat.dto;

import java.io.Serializable;

public class HotTopicDTO implements Serializable {

    private Long topicId;
    private double score;

    public HotTopicDTO() {}

    public HotTopicDTO(Long topicId, double score) {
        this.topicId = topicId;
        this.score = score;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
