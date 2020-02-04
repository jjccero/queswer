package com.gzu.queswer.controller;

import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.UserInfoApi;
import com.gzu.queswer.service.AnswerService;
import com.gzu.queswer.service.QuestionService;
import com.gzu.queswer.service.TopicService;
import com.gzu.queswer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return questionService.insertQuestion(question);
    }

    @RequestMapping(value = "getQuestions", method = RequestMethod.GET)
    public List getQuestions(int offset, int limit, Long uid) {
        List<UserInfoApi> questions = questionService.selectQuestions(offset, limit);
        userService.setUserInfo(questions, uid);
        return questions;
    }

    @RequestMapping("getQuestion")
    public Map getQuestion(Long qid, Long uid) {
        Map map = new HashMap();
        if (qid != null) {
            Question question = questionService.selectQuestionByQid(qid);
            userService.setUserInfo(question, uid);
            map.put("question", question);
            map.put("topics", topicService.selectQuestionTopics(qid));
            map.put("followCount",questionService.selectFollowCount(qid));

        }
        if (uid != null) {
            map.put("followed", questionService.isFollowed(qid, uid));
            map.put("questioned", questionService.isQuestioned(qid, uid));
            Answer answer=answerService.selectAnswerByUid(qid, uid);
            userService.setUserInfo(answer,uid);
            map.put("answer",answer);
        }
        else {
            map.put("followed", false);
            map.put("questioned", false);
            map.put("answer",null);
        }

        return map;
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
