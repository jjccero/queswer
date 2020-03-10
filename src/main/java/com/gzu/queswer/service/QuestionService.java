package com.gzu.queswer.service;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.dao.QuestionDaoImpl;
import com.gzu.queswer.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDaoImpl questionDaoImpl;

    public Long insertQuestion(Question question) {
        questionDaoImpl.insertQuestion(question);
        return question.getQid();
    }

    public JSONObject selectQuestionByQid(Long qid,Long uid) {
        return questionDaoImpl.getQuestionInfo(qid,uid);
    }

    public List selectQuestions(int offset, int limit) {
        return questionDaoImpl.selectQuestions(offset, limit);
    }

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

}
