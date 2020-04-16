package com.gzu.queswer.dao;

import com.gzu.queswer.model.Topic;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicDao {
    void insertTopic(Topic topic);

    List selectTopics();

    Topic selectTopicByTid(Long tId);

    List selectQuestionTopics(Long qId);
}
