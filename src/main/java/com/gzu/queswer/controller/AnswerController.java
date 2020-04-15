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
        answer.setAnsId(null);
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
    public boolean deleteAnswer(Long aid, Long uid) {
        return answerService.deleteAnswer(aid, uid);
    }

    @PostMapping("/updateAttitude")
    public boolean updateAttitude(@RequestBody Attitude attitude) {
        return answerService.updateAttitude(attitude);
    }

    @GetMapping("/deleteAttitude")
    public boolean deleteAttitude(long aid, long uid) {
        return answerService.deleteAttitude(aid, uid);
    }


    @GetMapping("/getAnswers")
    public List getAnswers(Long qid, Long uid) {
        return answerService.getAnswers(qid, uid);
    }
}
