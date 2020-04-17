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
    public List queryQuestions(int offset, int count, Long uId) {
        return questionService.queryQuestions(offset, count, uId);
    }

    @GetMapping("/getQuestion")
    public QuestionInfo getQuestion(Long qId, Long aId, Long uId) {
        return questionService.selectQuestionInfo(qId, aId, uId, true, true);
    }

    @GetMapping("/saveFollow")
    public boolean saveFollow(Long qId, Long uId) {
        return questionService.saveFollow(qId, uId);
    }

    @GetMapping("/deleteFollow")
    public boolean deleteFollow(Long qId, Long uId) {
        return questionService.deleteFollow(qId, uId);
    }


}
