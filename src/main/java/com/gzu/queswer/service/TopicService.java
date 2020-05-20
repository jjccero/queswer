package com.gzu.queswer.service;

import com.gzu.queswer.model.vo.QuestionInfo;
import com.gzu.queswer.model.vo.TopicInfo;

import java.util.List;
import java.util.Set;

public interface TopicService {
    boolean saveSubscribe(String topic, Long userId);

    boolean deleteSubscribe(String topic, Long userId);

    TopicInfo getTopicInfo(String topic, Long userId);

    Set<String> queryTopicsByUserId(Long userId);

    List<QuestionInfo> queryTopicQuestionInfosByUserId(Long userId);
}
