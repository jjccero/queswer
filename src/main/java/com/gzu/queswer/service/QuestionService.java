package com.gzu.queswer.service;

import com.gzu.queswer.dao.impl.QuestionDaoImpl;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.info.QuestionInfo;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDaoImpl questionDaoImpl;
    @Autowired
    UserService userService;
    @Autowired
    TopicService topicService;
    @Autowired
    AnswerService answerService;

    public Long saveQuestion(Question question) {
        question.setqId(null);
        question.setGmtCreate(DateUtil.getUnixTime());
        questionDaoImpl.insertQuestion(question);
        return question.getqId();
    }

    public QuestionInfo selectQuestionInfo(Long qId, Long aId, Long uId, boolean userAnswer, boolean view) {
        QuestionInfo questionInfo = questionDaoImpl.getQuestionInfo(qId, uId, view);
        questionInfo.setTopics(topicService.selectQuestionTopics(qId));
        Long userAId = null;
        if (uId != null && userAnswer) {
            userAId = questionDaoImpl.selectAidByUid(qId, uId);
            if (userAId != null) questionInfo.setUserAnswer(answerService.getAnswerInfo(userAId, uId));
        }
        if (aId != null && !aId.equals(userAId)) questionInfo.setDefaultAnswer(answerService.getAnswerInfo(aId, uId));
        setUserInfo(questionInfo, uId);
        return questionInfo;
    }

    public QuestionInfo selectQuestionInfo(Long qId, Long uid) {
        QuestionInfo questionInfo = questionDaoImpl.getQuestionInfo(qId, uid, false);
        questionInfo.setTopics(topicService.selectQuestionTopics(qId));
        Long aid = questionDaoImpl.getTopAid(qId);
        if (aid != null) questionInfo.setDefaultAnswer(answerService.getAnswerInfo(aid, uid));
        setUserInfo(questionInfo, uid);
        return questionInfo;
    }

    public List<QuestionInfo> queryQuestions(int offset, int count, Long uid) {
        List<Long> qIds = questionDaoImpl.queryQIds(offset, count);
        List<QuestionInfo> questionInfos = new ArrayList<>();
        for (Long qId : qIds) {
            Long aid = questionDaoImpl.getTopAid(qId);
            QuestionInfo questionInfo = selectQuestionInfo(qId, aid, uid, false, false);
            questionInfos.add(questionInfo);
        }
        return questionInfos;
    }

    public boolean saveFollow(Long qId, Long uid) {
        return questionDaoImpl.insertFollow(qId, uid) == 1;
    }

    public boolean deleteFollow(Long qId, Long uid) {
        return questionDaoImpl.deleteFollow(qId, uid) == 1;
    }

    public void setUserInfo(QuestionInfo questionInfo, Long uid) {
        if (questionInfo == null) return;
        Question question = questionInfo.getQuestion();
        UserInfo userInfo;
        Boolean anonymous = question.getAnonymous();
        if (Boolean.TRUE.equals(anonymous) && !question.getuId().equals(uid)) {
            userInfo = UserInfo.defaultUserInfo;
            question.setuId(null);
        } else {
            userInfo = userService.selectUserInfo(question.getuId(), uid);
            userInfo.setAnonymous(anonymous);
        }
        questionInfo.setUserInfo(userInfo);
    }

    public List<Long> selectAidsByQid(Long qid) {
        return questionDaoImpl.selectAIds(qid);
    }

    public Question selectQuestionByQid(Long qid) {
        return questionDaoImpl.selectQuestionByQid(qid);
    }


}
