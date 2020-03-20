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
        question.setGmt_create(DateUtil.getUnixTime());
        return questionService.insertQuestion(question);
    }

    @RequestMapping(value = "getQuestions", method = RequestMethod.GET)
    public List getQuestions(int offset, int count, Long uid) {
        return questionService.selectQuestions(offset, count, uid);
    }

    @RequestMapping("getQuestion")
    public QuestionInfo getQuestion(Long qid, Long aid, Long uid) {
        return questionService.getQuestionInfo(qid, aid, uid, true);
    }

    @RequestMapping("insertFollow")
    public boolean insertFollow(Long qid, Long uid) {
        return questionService.insertFollow(qid, uid);
    }

    @RequestMapping("deleteFollow")
    public boolean deleteFollow(Long qid, Long uid) {
        return questionService.deleteFollow(qid, uid);
    }


}
