package com.gzu.queswer.controller;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.common.UserContext;
import com.gzu.queswer.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {
    @Autowired
    UserContext userContext;
    @Autowired
    CacheService cacheService;

    @GetMapping("/createIndex")
    public boolean createIndex() {
        return cacheService.createIndex();
    }

    @GetMapping("/searchQuestionInfos")
    public JSONObject searchQuestionInfos(String title, Long userId) {
        return cacheService.selectQuestionInfosByQuestion(title, userId);
    }

    @GetMapping("/searchUserInfos")
    public List searchUserInfos(String nickname, Long userId) {
        return cacheService.selectUserInfosByNickname(nickname, userId);
    }

    @GetMapping("/restore")
    public boolean restore() {
        return cacheService.restore();
    }

    @GetMapping("/flush")
    public boolean flush() {
        return cacheService.flush();
    }

    @GetMapping("/backup")
    public boolean backup() {
        return cacheService.backup();
    }

}
