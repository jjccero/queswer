package com.gzu.queswer.service;

import com.gzu.queswer.model.Topic;

import java.util.List;

public interface TopicService {
    List<Topic> queryTopics();

    Long saveTopic(Topic topic);

    Topic getTopic(Long topicId);

    List<Topic> queryTopicsByQuestionId(Long questionId);
}
