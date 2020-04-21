package com.gzu.queswer.controller;

import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.info.QuestionInfo;
import com.gzu.queswer.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuestionController {
    @Autowired
    QuestionService questionService;

    @PostMapping(value = "/saveQuestion")
    public Long saveQuestion(@RequestBody Question question) {
        return questionService.saveQuestion(question);
    }

    @GetMapping(value = "/queryQuestions")
    public List queryQuestions(int offset, int limit, Long userId) {
        return questionService.queryQuestions(offset, limit, userId);
    }

    @GetMapping("/getQuestion")
    public QuestionInfo getQuestion(Long questionId, Long answerId, Long userId) {
        return questionService.getQuestionInfo(questionId, answerId, userId, true, true);
    }

    @GetMapping("/saveSubscribe")
    public boolean saveSubscribe(Long questionId, Long userId) {
        return questionService.saveSubscribe(questionId, userId);
    }

    @GetMapping("/deleteSubscribe")
    public boolean deleteSubscribe(Long questionId, Long userId) {
        return questionService.deleteSubscribe(questionId, userId);
    }
}
