package com.gzu.queswer.model;

import java.io.Serializable;

public class Topic implements Serializable {
    private Long tId;
    private String topicName;

    public Long gettId() {
        return tId;
    }

    public void settId(Long tId) {
        this.tId = tId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
