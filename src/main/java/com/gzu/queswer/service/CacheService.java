package com.gzu.queswer.service;

import com.gzu.queswer.dao.impl.CacheDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CacheService {
    @Autowired
    CacheDaoImpl cacheDaoImpl;

    public boolean createIndex() {
        return cacheDaoImpl.createIndex();
    }

    @Autowired
    QuestionService questionService;

    public List selectQuestionInfosByQuestion(String question, Long uid) {
        List<Long> qids = cacheDaoImpl.selectQidsByQuestion(question);
        List questionInfos = new ArrayList(qids.size());
        for (Long qid : qids) {
            questionInfos.add(questionService.selectQuestionInfo(qid, uid));
        }
        return questionInfos;
    }

    @Autowired
    UserService userService;

    public List selectUserInfosByNickname(String nickname, Long uid) {
        List<Long> uids = cacheDaoImpl.selectUserInfosByNickname(nickname);
        List userInfos = new ArrayList(uids.size());
        for (Long uid0 : uids) {
            userInfos.add(userService.selectUserInfo(uid0, uid));
        }
        return userInfos;
    }

    public boolean initRedis() {
        return cacheDaoImpl.initRedis();
    }
}
