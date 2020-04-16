package com.gzu.queswer.controller;

import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.info.QuestionInfo;
import com.gzu.queswer.service.AnswerService;
import com.gzu.queswer.service.QuestionService;
import com.gzu.queswer.service.TopicService;
import com.gzu.queswer.service.UserService;
import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/insertQuestion")
    public Long insertQuestion(@RequestBody Question question) {
        question.setqId(null);
        question.setGmtCreate(DateUtil.getUnixTime());
        return questionService.insertQuestion(question);
    }

    @GetMapping(value = "/queryQuestions")
    public List getQuestions(int offset, int count, Long uId) {
        return questionService.selectQuestions(offset, count, uId);
    }

    @GetMapping("/getQuestion")
    public QuestionInfo getQuestion(Long qId, Long aId, Long uId) {
        return questionService.selectQuestionInfo(qId, aId, uId, true, true);
    }

    @GetMapping("/insertFollow")
    public boolean insertFollow(Long qId, Long uId) {
        return questionService.insertFollow(qId, uId);
    }

    @GetMapping("/deleteFollow")
    public boolean deleteFollow(Long qId, Long uId) {
        return questionService.deleteFollow(qId, uId);
    }


}
