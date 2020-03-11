package com.gzu.queswer.service;

import com.gzu.queswer.dao.QuestionDaoImpl;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.UserInfo;
import com.gzu.queswer.model.info.QuestionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDaoImpl questionDaoImpl;
    @Autowired
    UserService userService;
    @Autowired
    TopicService topicService;

    public Long insertQuestion(Question question) {
        questionDaoImpl.insertQuestion(question);
        return question.getQid();
    }

    public QuestionInfo getQuestionInfo(Long qid, Long uid) {
        QuestionInfo questionInfo = questionDaoImpl.getQuestionInfo(qid, uid);
        questionInfo.setTopics(topicService.selectQuestionTopics(qid));
        questionInfo.setAnswer(questionDaoImpl.selectAnswerByUid(qid, uid));
        setUserInfo(questionInfo, uid);
        return questionInfo;
    }

    public List selectQuestions(int offset, int limit) {
        return questionDaoImpl.selectQuestions(offset, limit);
    }

    @Deprecated
    public Integer selectFollowCount(Long qid) {
        return questionDaoImpl.selectFollowCount(qid);
    }

    public Integer insertFollow(Long qid, Long uid) {
        return questionDaoImpl.insertFollow(qid, uid);
    }

    public Integer deleteFollow(Long qid, Long uid) {
        return questionDaoImpl.deleteFollow(qid, uid);
    }

    public List selectFollowsByUid(Long uid) {
        return questionDaoImpl.selectFollowsByUid(uid);
    }

    public void setUserInfo(QuestionInfo questionInfo, Long uid) {
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
