package com.gzu.queswer.controller;

import com.gzu.queswer.model.Topic;
import com.gzu.queswer.service.CacheService;
import com.gzu.queswer.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {
    @Autowired
    TopicService topicService;

    @GetMapping("/queryTopics")
    public List queryTopics() {
        return topicService.queryTopics();
    }

    @PostMapping("/saveTopic")
    public Long saveTopic(@RequestBody Topic topic) {
        return topicService.saveTopic(topic);
    }


    @Autowired
    CacheService cacheService;

    @GetMapping("/createIndex")
    public boolean createIndex() {
        return cacheService.createIndex();
    }

    @GetMapping("/searchQuestionInfos")
    public List searchQuestionInfos(String title, Long uId) {
        return cacheService.selectQuestionInfosByQuestion(title, uId);
    }

    @GetMapping("/searchUserInfos")
    public List searchUserInfos(String nickname, Long uId) {
        return cacheService.selectUserInfosByNickname(nickname, uId);
    }

    @GetMapping("/init")
    public boolean initRedis() {
        return cacheService.initRedis();
    }
}
