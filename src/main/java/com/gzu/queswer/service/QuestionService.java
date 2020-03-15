package com.gzu.queswer.service;

import com.gzu.queswer.dao.daoImpl.QuestionDaoImpl;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.UserInfo;
import com.gzu.queswer.model.info.QuestionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public Long insertQuestion(Question question) {
        questionDaoImpl.insertQuestion(question);
        return question.getQid();
    }

    public QuestionInfo getQuestionInfo(Long qid, Long aid, Long uid, boolean user_answer) {
        QuestionInfo questionInfo = questionDaoImpl.getQuestionInfo(qid, uid);
        questionInfo.setTopics(topicService.selectQuestionTopics(qid));
        Long user_aid = null;
        if (uid != null && user_answer) {
            user_aid = questionDaoImpl.selectAidByUid(qid, uid);
            if (user_aid != null) questionInfo.setUserAnswer(answerService.getAnswerInfo(user_aid, uid));
        }
        if (aid != user_aid && aid != null) questionInfo.setDefaultAnswer(answerService.getAnswerInfo(aid, uid));
        setUserInfo(questionInfo, uid);
        return questionInfo;
    }

    public List selectQuestions(int offset, int count, Long uid) {
        Set<String> qid_keys = questionDaoImpl.getQids(offset, count);
        List<QuestionInfo> questionInfos = new ArrayList<>();
        for (String qid_key : qid_keys) {
            Long qid = Long.parseLong(qid_key);
            Long aid = questionDaoImpl.getTopAid(qid);
            QuestionInfo questionInfo = getQuestionInfo(qid, aid, uid, false);
            questionInfos.add(questionInfo);
        }
        return questionInfos;
    }

    @Deprecated
    public Integer selectFollowCount(Long qid) {
        return questionDaoImpl.selectFollowCount(qid);
    }

    public boolean insertFollow(Long qid, Long uid) {
        return questionDaoImpl.insertFollow(qid, uid)==1;
    }

    public boolean deleteFollow(Long qid, Long uid) {
        return questionDaoImpl.deleteFollow(qid, uid)==1;
    }

    public List selectFollowsByUid(Long uid) {
        return questionDaoImpl.selectFollowsByUid(uid);
    }

    public void setUserInfo(QuestionInfo questionInfo, Long uid) {
        if (questionInfo == null) return;
        Question question = questionInfo.getQuestion();
        UserInfo userInfo;
        Boolean anonymous = question.getAnonymous();
        if (anonymous && !question.getUid().equals(uid)) {
            userInfo = UserInfo.defaultUserInfo;
            question.setUid(null);
        } else {
            userInfo = userService.getUserInfo(question.getUid());
            userInfo.setAnonymous(anonymous);
        }
        questionInfo.setUserInfo(userInfo);
    }

    public List selectAidsByQid(Long qid) {
        return questionDaoImpl.selectAidsByQid(qid);
    }

    public Question selectQuestionByQid(Long qid) {
        return questionDaoImpl.selectQuestionByQid(qid);
    }

    public Long insertAnswer(Answer answer) {
        Long aid = answer.getAid();
        if (aid != null) {
            questionDaoImpl.insertAnswer(answer.getQid().toString(), aid.toString());
        }
        return aid;
    }

    public boolean deleteAnswer(Answer answer) {
        return questionDaoImpl.deleteAnswer(answer.getQid().toString(), answer.getAid().toString());
    }
}
