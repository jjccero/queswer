package com.gzu.queswer.service;

import com.gzu.queswer.dao.daoImpl.CacheDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CacheService {
    @Autowired
    CacheDaoImpl cacheDaoImpl;

    public void createIndex(){
        cacheDaoImpl.createIndex();
    }

    @Autowired
    QuestionService questionService;
    public List selectQuestionInfosByQuestion(String question, Long uid){
        List<Long> qids= cacheDaoImpl.selectQidsByQuestion(question);
        List questionInfos=new ArrayList();
        for(Long qid:qids){
            questionInfos.add(questionService.selectQuestionInfo(qid,uid));
        }
        return questionInfos;
    }
}
