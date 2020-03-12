package com.gzu.queswer.controller;

import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.service.AnswerService;
import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @RequestMapping(value = "/addAnswer", method = RequestMethod.POST)
    public Long addAnswer(@RequestBody Answer answer) {
        answer.setAnswer_time(DateUtil.getUnixTime());
        answer.setAid(null);
        if(answer.getAnonymous()==null) answer.setAnonymous(false);
        return answerService.insertAnswer(answer);
    }

    @RequestMapping(value = "/updateAnswer", method = RequestMethod.POST)
    public boolean updateAnswer(@RequestBody Answer answer) {
        answer.setModify_answer_time(DateUtil.getUnixTime());
        if(answer.getAnonymous()==null) answer.setAnonymous(false);
        return answerService.updateAnswer(answer);
    }

    @RequestMapping("/deleteAnswer")
    public boolean deleteAnswer(Long aid, Long uid) {
        return answerService.deleteAnswer(aid, uid);
    }

    @RequestMapping("/addAttitude")
    public boolean addAttitude(@RequestBody Attitude attitude) {
        return answerService.insertAttitude(attitude);
    }

    @RequestMapping("/deleteAttitude")
    public boolean deleteAttitude(long aid, long uid) {
        return answerService.deleteAttitude(aid, uid);
    }


    @RequestMapping("getAnswers")
    public List getAnswers(Long qid, Long uid) {
        List list = answerService.getAnswers(qid,uid);
        return list;
    }
}
