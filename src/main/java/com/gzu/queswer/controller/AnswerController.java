package com.gzu.queswer.controller;

import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.service.AnswerService;
import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @PostMapping(value = "/insertAnswer")
    public Long insertAnswer(@RequestBody Answer answer) {
        answer.setGmtCreate(DateUtil.getUnixTime());
        answer.setaId(null);
        if (answer.getAnonymous() == null) answer.setAnonymous(false);
        return answerService.insertAnswer(answer);
    }

    @PostMapping(value = "/updateAnswer")
    public boolean updateAnswer(@RequestBody Answer answer) {
        answer.setGmtModify(DateUtil.getUnixTime());
        if (answer.getAnonymous() == null) answer.setAnonymous(false);
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


    @GetMapping("/getAnswers")
    public List getAnswers(Long qId, Long uId) {
        return answerService.getAnswers(qId, uId);
    }
}
