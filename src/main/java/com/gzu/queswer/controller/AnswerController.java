package com.gzu.queswer.controller;

import com.gzu.queswer.common.UserContext;
import com.gzu.queswer.common.UserException;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.vo.AnswerInfo;
import com.gzu.queswer.model.vo.QuestionInfo;
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
    @Autowired
    UserContext userContext;

    @PostMapping(value = "/saveAnswer")
    public Long saveAnswer(@RequestBody Answer answer) throws UserException {
        answer.setUserId(userContext.getUserId(true));
        return answerService.saveAnswer(answer);
    }

    @PostMapping(value = "/updateAnswer")
    public boolean updateAnswer(@RequestBody Answer answer) throws UserException {
        answer.setUserId(userContext.getUserId(true));
        return answerService.updateAnswer(answer);
    }

    @GetMapping("/deleteAnswer")
    public boolean deleteAnswer(Long answerId) throws UserException {
        return answerService.deleteAnswer(answerId, userContext.getUserId(true));
    }

    @PostMapping("/updateAttitude")
    public boolean updateAttitude(@RequestBody Attitude attitude) throws UserException {
        attitude.setUserId(userContext.getUserId(true));
        return answerService.updateAttitude(attitude);
    }

    @GetMapping("/deleteAttitude")
    public boolean deleteAttitude(long answerId) throws UserException {
        return answerService.deleteAttitude(answerId, userContext.getUserId(true));
    }

    @GetMapping("/queryAnswers")
    public List<AnswerInfo> queryAnswers(Long questionId) throws UserException {
        return answerService.queryAnswers(questionId, userContext.getUserId(false));
    }

    @GetMapping("/queryAnswersByUserId")
    public List<QuestionInfo> queryAnswersByUserId(Long peopleId) throws UserException {
        return answerService.queryAnswersByUserId(peopleId, userContext.getUserId(true));
    }
}
