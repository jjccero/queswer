package com.gzu.queswer.controller;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.UserInfoApi;
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
        question.setQuestion_time(DateUtil.getUnixTime());
        return questionService.insertQuestion(question);
    }

    @RequestMapping(value = "getQuestions", method = RequestMethod.GET)
    public List getQuestions(int offset, int limit, Long uid) {
        List<UserInfoApi> questions = questionService.selectQuestions(offset, limit);
        userService.setUserInfo(questions, uid);
        return questions;
    }

    @RequestMapping("getQuestion")
    public JSONObject getQuestion(Long qid, Long uid) {
        JSONObject jsonObject =questionService.selectQuestionByQid(qid,uid);;
        if (qid != null) {
            Question question = jsonObject.getObject("question",Question.class);
            userService.setUserInfo(question, uid);
            jsonObject.put("topics", topicService.selectQuestionTopics(qid));
        }
        if (uid != null) {
            Answer answer = answerService.selectAnswerByUid(qid, uid);
            userService.setUserInfo(answer, uid);
            jsonObject.put("answer", answer);
        } else {
            jsonObject.put("answer", null);
        }
        return jsonObject;
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
