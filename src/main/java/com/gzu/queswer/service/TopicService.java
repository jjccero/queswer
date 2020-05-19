package com.gzu.queswer.service;

import com.gzu.queswer.model.vo.TopicInfo;

public interface TopicService {
    boolean saveSubscribe(String topic, Long userId);

    boolean deleteSubscribe(String topic, Long userId);

    TopicInfo getTopicInfo(String topic, Long userId);
}
