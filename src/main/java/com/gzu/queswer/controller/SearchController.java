package com.gzu.queswer.controller;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.common.UserContext;
import com.gzu.queswer.common.UserException;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {
    @Autowired
    UserContext userContext;
    @Autowired
    SearchService searchService;

    @GetMapping("/createIndex")
    public boolean createIndex() throws UserException {
        userContext.check(UserLogin.SUPER_ADMIN, true);
        return searchService.createIndex();
    }

    @GetMapping("/searchQuestionInfos")
    public JSONObject searchQuestionInfos(String title) throws UserException {
        return searchService.selectQuestionInfosByQuestion(title, userContext.getUserId(false));
    }

    @GetMapping("/searchUserInfos")
    public List searchUserInfos(String nickname) throws UserException {
        return searchService.selectUserInfosByNickname(nickname, userContext.getUserId(false));
    }

}
