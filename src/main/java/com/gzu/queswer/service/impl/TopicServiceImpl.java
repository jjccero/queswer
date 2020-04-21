package com.gzu.queswer.service.impl;

import com.gzu.queswer.dao.TopicDao;
import com.gzu.queswer.model.Topic;
import com.gzu.queswer.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 蒋竟成
 * @date 2020/4/19
 */
@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    TopicDao topicDao;

    @Override
    public List<Topic> queryTopics() {
        return topicDao.selectTopics();
    }

    @Override
    public Long saveTopic(Topic topic) {
        topic.setTopicId(null);
        topicDao.insertTopic(topic);
        return topic.getTopicId();
    }

    @Override
    public Topic getTopic(Long topicId) {
        return topicDao.selectTopic(topicId);
    }

    @Override
    public List<Topic> queryTopicsByQuestionId(Long questionId) {
        List<Long> topicIds = topicDao.selectTopicIdsByQuestionId(questionId);
        List<Topic> topics = new ArrayList<>(topicIds.size());
        for (Long topicId : topicIds) {
            topics.add(topicDao.selectTopic(topicId));
        }
        return topics;
    }
}
