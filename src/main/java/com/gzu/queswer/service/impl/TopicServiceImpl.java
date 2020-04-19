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
        topic.settId(null);
        topicDao.insertTopic(topic);
        return topic.gettId();
    }

    @Override
    public Topic getTopicByTId(Long tId) {
        return topicDao.selectTopicByTId(tId);
    }

    @Override
    public List<Topic> queryTopicsByQId(Long qId) {
        List<Long> tIds = topicDao.selectTIdsByQId(qId);
        List<Topic> topics = new ArrayList<>(tIds.size());
        for (Long tId : tIds) {
            topics.add(topicDao.selectTopicByTId(tId));
        }
        return topics;
    }
}
