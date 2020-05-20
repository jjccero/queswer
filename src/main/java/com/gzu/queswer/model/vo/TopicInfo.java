package com.gzu.queswer.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TopicInfo {
    private String topic;
    private Long subscribeCount;
    private Boolean subscribed;
    private List<QuestionInfo> questionInfos;
}
