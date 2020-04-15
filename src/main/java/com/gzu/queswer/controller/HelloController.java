package com.gzu.queswer.controller;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.Topic;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.service.CacheService;
import com.gzu.queswer.service.TopicService;
import com.gzu.queswer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HelloController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/deleteCache")
    public int deleteCache() {
        return 1;
    }

    @PostMapping(value = "/login")
    public User login(@RequestBody JSONObject loginForm) {
        String username = loginForm.getString("username");
        String password = loginForm.getString("password");
        return userService.login(username, password);
    }

    @PostMapping(value = "/signup")
    public Long signup(@RequestBody UserLogin userLogin) {
        userLogin.setNormalUser();
        return userService.insertUser(userLogin);
    }

    @PostMapping("/signupSuper")
    public Long signupSuper(@RequestBody UserLogin userLogin) {
        userLogin.setSuperUser();
        return userService.insertUser(userLogin);
    }

    @PostMapping("/updateUser")
    public Integer updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping(value = "/selectUserInfo")
    public UserInfo selectUserInfo(Long people_uid, Long uid) {
        return userService.selectUserInfo(people_uid, uid);
    }

    @Autowired
    TopicService topicService;

    @GetMapping("/getTopicList")
    public List getTopicList() {
        return topicService.selectTopics();
    }

    @PostMapping("addTopic")
    public Long addTopic(@RequestBody Topic topic) {
        return topicService.insertTopic(topic);
    }


    @Autowired
    CacheService cacheService;

    @GetMapping("/createIndex")
    public boolean createIndex() {
        return cacheService.createIndex();
    }

    @GetMapping("/searchQuestionInfos")
    public List searchQuestionInfos(String question, Long uid) {
        return cacheService.selectQuestionInfosByQuestion(question, uid);
    }

    @GetMapping("/searchUserInfos")
    public List searchUserInfos(String nickname, Long uid) {
        return cacheService.selectUserInfosByNickname(nickname, uid);
    }
}
