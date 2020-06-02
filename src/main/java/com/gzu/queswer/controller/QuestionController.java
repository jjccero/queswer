package com.gzu.queswer.controller;

import com.gzu.queswer.common.UserContext;
import com.gzu.queswer.common.UserException;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.vo.ActivityInfo;
import com.gzu.queswer.model.vo.QuestionInfo;
import com.gzu.queswer.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class QuestionController {
    @Autowired
    QuestionService questionService;
    @Autowired
    UserContext userContext;

    @PostMapping(value = "/saveQuestion")
    public Long saveQuestion(@RequestBody Question question) throws UserException {
        question.setUserId(userContext.getUserId(true));
        return questionService.saveQuestion(question);
    }

    @PostMapping(value = "/updateQuestion")
    public boolean updateQuestion(@RequestBody Question question) throws UserException {
        userContext.check(UserLogin.ADMIN, true);
        return questionService.updateQuestion(question);
    }

    @GetMapping(value = "/queryQuestions")
    public List queryQuestions(int page, int limit) throws UserException {
        return questionService.queryQuestions(page, limit, userContext.getUserId(false));
    }

    @GetMapping(value = "/deleteQuestion")
    public boolean deleteQuestion(Long questionId) throws UserException {
        userContext.check(UserLogin.ADMIN, true);
        return questionService.deleteQuestion(questionId);
    }

    @GetMapping("/getQuestion")
    public QuestionInfo getQuestion(Long questionId, Long answerId) throws UserException {
        return questionService.getQuestionInfo(questionId, answerId, userContext.getUserId(false), true, true);
    }

    @GetMapping("/saveSubscribeQuestion")
    public boolean saveSubscribe(Long questionId) throws UserException {
        return questionService.saveSubscribeQuestion(questionId, userContext.getUserId(true));
    }

    @GetMapping("/deleteSubscribeQuestion")
    public boolean deleteSubscribe(Long questionId) throws UserException {
        return questionService.deleteSubscribeQuestion(questionId, userContext.getUserId(true));
    }

    @GetMapping("/queryQuestionsByUserId")
    public List<QuestionInfo> queryQuestionsByUserId(Long peopleId) throws UserException {
        return questionService.queryQuestionsByUserId(peopleId, userContext.getUserId(false));
    }

    @GetMapping("/querySubscribeQuestionsByUserId")
    public List<ActivityInfo> querySubscribeQuestionsByUserId(Long peopleId) throws UserException {
        return questionService.querySubscribeQuestionsByUserId(peopleId, userContext.getUserId(false));
    }
}
