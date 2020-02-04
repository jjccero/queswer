package com.gzu.queswer.service;

import com.gzu.queswer.dao.QuestionDao;
import com.gzu.queswer.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDao questionDao;

    public Long insertQuestion(Question question) {
        questionDao.insertQuestion(question);
        return question.getQid();
    }

    public Question selectQuestionByQid(Long qid) {
        return questionDao.selectQuestionByQid(qid);
    }

    public List selectQuestions(int offset, int limit) {
        return questionDao.selectQuestions(offset, limit);
    }

    public Integer selectFollowCount(Long qid) {
        return questionDao.selectFollowCount(qid);
    }

    public Integer insertFollow(Long qid, Long uid) {
        return questionDao.insertFollow(qid, uid);
    }

    public Integer deleteFollow(Long qid, Long uid) {
        return questionDao.deleteFollow(qid, uid);
    }

    public Boolean isFollowed(Long qid, Long uid) {
        return questionDao.isFollowed(qid, uid);
    }

    public Boolean isQuestioned(Long qid, Long uid) {
        return questionDao.isQuestioned(qid, uid);
    }

    public List selectFollowsByUid(Long uid) {
        return questionDao.selectFollowsByUid(uid);
    }
}
