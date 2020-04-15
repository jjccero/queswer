package com.gzu.queswer.service;

import com.gzu.queswer.dao.TopicDao;
import com.gzu.queswer.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {
    @Autowired
    TopicDao topicDao;

    public List selectTopics() {
        return topicDao.selectTopics();
    }

    public Long insertTopic(Topic topic) {
        topicDao.insertTopic(topic);
        return topic.gettId();
    }

    public Topic selectTopicByTid(Long tid) {
        return topicDao.selectTopicByTid(tid);
    }

    public List selectQuestionTopics(Long qid) {
        List topics = topicDao.selectQuestionTopics(qid);
        for (int i = 0; i < topics.size(); ++i) {
            Long tid = (Long) topics.get(i);
            topics.set(i, selectTopicByTid(tid));
        }
        return topics;
    }
}
