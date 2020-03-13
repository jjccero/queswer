package com.gzu.queswer.controller;

import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.info.QuestionInfo;
import com.gzu.queswer.service.AnswerService;
import com.gzu.queswer.service.QuestionService;
import com.gzu.queswer.service.TopicService;
import com.gzu.queswer.service.UserService;
import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuestionController {
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    TopicService topicService;
    @Autowired
    AnswerService answerService;

    @RequestMapping(value = "insertQuestion", method = RequestMethod.POST)
    public Long insertQuestion(@RequestBody Question question) {
        question.setQid(null);
        question.setQuestion_time(DateUtil.getUnixTime());
        return questionService.insertQuestion(question);
    }

    @RequestMapping(value = "getQuestions", method = RequestMethod.GET)
    public List getQuestions(int offset, int limit, Long uid) {
        return questionService.selectQuestions(offset, limit,uid);
    }

    @RequestMapping("getQuestion")
    public QuestionInfo getQuestion(Long qid, Long uid,Long aid) {
        return questionService.getQuestionInfo(qid,uid,aid,true);
    }

    @RequestMapping("insertFollow")
    public Integer insertFollow(Long qid, Long uid) {
        return questionService.insertFollow(qid, uid);
    }

    @RequestMapping("deleteFollow")
    public Integer deleteFollow(Long qid, Long uid) {
        return questionService.deleteFollow(qid, uid);
    }



}
