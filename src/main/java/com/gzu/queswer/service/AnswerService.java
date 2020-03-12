package com.gzu.queswer.service;

import com.gzu.queswer.dao.daoImpl.AnswerDaoImpl;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.UserInfo;
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
        answerDaoImpl.insertAnswer(answer);
        return questionService.insertAnswer(answer);
    }

    public boolean deleteAnswer(Long aid, Long uid) {
        Answer answer = answerDaoImpl.selectAnswerByAid(aid);
        return answer != null && answer.getUid().equals(uid) && questionService.deleteAnswer(answer) && answerDaoImpl.deleteAnswer(aid, uid);
    }

    public boolean updateAnswer(Answer answer) {
        return answerDaoImpl.updateAnswer(answer);
    }

    public boolean insertAttitude(Attitude attitude) {
        return answerDaoImpl.insertAttitude(attitude);
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

    public void setUserInfo(AnswerInfo answerInfo, Long uid) {
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

    public Long insertReview(Review review) {
        Long rid = review.getRid();
        if (rid != null) {
            answerDaoImpl.addReview(review.getAid().toString(), rid.toString());
        }
        return rid;
    }
}
