package com.gzu.queswer.controller;

import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.info.AnswerInfo;
import com.gzu.queswer.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @PostMapping(value = "/saveAnswer")
    public Long saveAnswer(@RequestBody Answer answer) {
        return answerService.saveAnswer(answer);
    }

    @PostMapping(value = "/updateAnswer")
    public boolean updateAnswer(@RequestBody Answer answer) {
        return answerService.updateAnswer(answer);
    }

    @GetMapping("/deleteAnswer")
    public boolean deleteAnswer(Long aId, Long uId) {
        return answerService.deleteAnswer(aId, uId);
    }

    @PostMapping("/updateAttitude")
    public boolean updateAttitude(@RequestBody Attitude attitude) {
        return answerService.updateAttitude(attitude);
    }

    @GetMapping("/deleteAttitude")
    public boolean deleteAttitude(long aId, long uId) {
        return answerService.deleteAttitude(aId, uId);
    }


    @GetMapping("/queryAnswers")
    public List<AnswerInfo> queryAnswers(Long qId, Long uId) {
        return answerService.queryAnswers(qId, uId);
    }
}
