package com.gzu.queswer.service;

import com.gzu.queswer.dao.daoImpl.AnswerDaoImpl;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.model.info.AnswerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerService {

    @Autowired
    private AnswerDaoImpl answerDaoImpl;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;

    public Long insertAnswer(Answer answer) {
        return answerDaoImpl.insertAnswer(answer);
    }

    public boolean deleteAnswer(Long aid, Long uid) {
        return answerDaoImpl.deleteAnswer(aid, uid);
    }

    public boolean updateAnswer(Answer answer) {
        return answerDaoImpl.updateAnswer(answer);
    }

    public boolean updateAttitude(Attitude attitude) {
        return answerDaoImpl.updateAttitude(attitude);
    }

    public boolean deleteAttitude(long aid, long uid) {
        return answerDaoImpl.deleteAttitude(aid, uid);
    }

    public List getAnswers(Long qid, Long uid) {
        List<Long> aids = questionService.selectAidsByQid(qid);
        List<AnswerInfo> answerInfos = new ArrayList<>();
        for (Long aid : aids) {
            AnswerInfo answerInfo = answerDaoImpl.getAnswerInfo(aid, uid);
            setUserInfo(answerInfo, uid);
            answerInfos.add(answerInfo);
        }
        return answerInfos;
    }

    public AnswerInfo getAnswerInfo(Long aid, Long uid) {
        AnswerInfo answerInfo = answerDaoImpl.getAnswerInfo(aid, uid);
        setUserInfo(answerInfo, uid);
        return answerInfo;
    }

    public void setUserInfo(AnswerInfo answerInfo, Long uid) {
        if (answerInfo == null) return;
        UserInfo userInfo;
        Answer answer = answerInfo.getAnswer();
        Boolean anonymous = answer.getAnonymous();
        if (anonymous && !answer.getUid().equals(uid)) {
            userInfo = UserInfo.defaultUserInfo;
            answer.setUid(null);
        } else {
            userInfo = userService.getUserInfo(answer.getUid());
            userInfo.setAnonymous(anonymous);
        }
        answerInfo.setUserInfo(userInfo);
    }

    public Answer selectAnswerByAid(Long aid) {
        return answerDaoImpl.selectAnswerByAid(aid);
    }

    public List selectRidsByAid(Long aid) {
        return answerDaoImpl.selectRidsByAid(aid);
    }

}
