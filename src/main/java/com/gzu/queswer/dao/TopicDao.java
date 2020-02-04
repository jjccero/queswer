package com.gzu.queswer.dao;

import com.gzu.queswer.model.Topic;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicDao {
    void insertTopic(Topic topic);

    List selectTopics();

    Topic selectTopicByTid(@Param("tid") Long tid);

    List selectQuestionTopics(@Param("qid") Long qid);
}
