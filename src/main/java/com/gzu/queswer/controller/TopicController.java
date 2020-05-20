package com.gzu.queswer.controller;

import com.gzu.queswer.common.UserContext;
import com.gzu.queswer.common.UserException;
import com.gzu.queswer.model.vo.QuestionInfo;
import com.gzu.queswer.model.vo.TopicInfo;
import com.gzu.queswer.service.ActivityService;
import com.gzu.queswer.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
public class TopicController {
    @Autowired
    TopicService topicService;
    @Autowired
    ActivityService activityService;
    @Autowired
    UserContext userContext;

    @GetMapping("/saveSubscribeTopic")
    public boolean saveSubscribe(String topic) throws UserException {
        return topicService.saveSubscribe(topic, userContext.getUserId(true));
    }

    @GetMapping("/deleteSubscribeTopic")
    public boolean deleteSubscribe(String topic) throws UserException {
        return topicService.deleteSubscribe(topic, userContext.getUserId(true));
    }

    @GetMapping("/getTopicInfo")
    public TopicInfo getTopicInfo(String topic) throws UserException {
        return topicService.getTopicInfo(topic, userContext.getUserId(false));
    }

    @GetMapping("/queryTopicsByUserId")
    public Set<String> queryTopicsByUserId() throws UserException {
        return topicService.queryTopicsByUserId(userContext.getUserId(true));
    }

    @GetMapping("/queryTopicQuestionInfosByUserId")
    public List<QuestionInfo> queryTopicQuestionInfosByUserId() throws UserException {
        return topicService.queryTopicQuestionInfosByUserId(userContext.getUserId(true));
    }
}
