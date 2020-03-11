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

    @RequestMapping(value = "addQuestion", method = RequestMethod.POST)
    public Long addQuestion(@RequestBody Question question) {
        question.setQid(null);
        question.setQuestion_time(DateUtil.getUnixTime());
        return questionService.insertQuestion(question);
    }

    @RequestMapping(value = "getQuestions", method = RequestMethod.GET)
    public List getQuestions(int offset, int limit, Long uid) {
        List<Question> questions = questionService.selectQuestions(offset, limit);
        return questions;
    }

    @RequestMapping("getQuestion")
    public QuestionInfo getQuestion(Long qid, Long uid) {
        return questionService.getQuestionInfo(qid,uid);
    }

    @RequestMapping("addFollow")
    public Integer addFollow(Long qid, Long uid) {
        return questionService.insertFollow(qid, uid);
    }

    @RequestMapping("deleteFollow")
    public Integer deleteFollow(Long qid, Long uid) {
        return questionService.deleteFollow(qid, uid);
    }

    @RequestMapping(value = "getOpptunities", method = RequestMethod.GET)
    public List getOpptunities(int offset, int limit) {
        return null;
    }


}
