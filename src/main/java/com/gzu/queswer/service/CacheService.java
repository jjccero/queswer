package com.gzu.queswer.service;

import com.gzu.queswer.dao.impl.CacheServiceImpl;
import com.gzu.queswer.model.info.QuestionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CacheService {
    @Autowired
    CacheServiceImpl cacheDaoImpl;

    public boolean createIndex() {
        return cacheDaoImpl.createIndex();
    }

    @Autowired
    QuestionService questionService;

    public List selectQuestionInfosByQuestion(String ques, Long uid) {
        List<Long> qids = cacheDaoImpl.selectQidsByQuestion(ques);
        List<QuestionInfo> questionInfos = new ArrayList<>(qids.size());
        for (Long qid : qids) {
            questionInfos.add(questionService.getQuestionInfo(qid, uid, false));
        }
        return questionInfos;
    }

    @Autowired
    UserService userService;

    public List selectUserInfosByNickname(String nickname, Long uid) {
        List<Long> uids = cacheDaoImpl.selectUserInfosByNickname(nickname);
        List userInfos = new ArrayList(uids.size());
        for (Long uid0 : uids) {
            userInfos.add(userService.getUserInfo(uid0, uid));
        }
        return userInfos;
    }

    public boolean initRedis() {
        return cacheDaoImpl.initRedis();
    }
}
