package com.gzu.queswer.dao;

import com.gzu.queswer.model.Topic;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicDao {
    void insertTopic(Topic topic);

    List<Topic> selectTopics();

    Topic selectTopic(Long topicId);

    List<Long> selectTopicIdsByQuestionId(Long questionId);
}
