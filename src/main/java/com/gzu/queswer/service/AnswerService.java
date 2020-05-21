package com.gzu.queswer.service;

import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.vo.AnswerInfo;
import com.gzu.queswer.model.vo.QuestionInfo;

import java.util.List;

public interface AnswerService {
    Long saveAnswer(Answer answer);

    boolean deleteAnswer(Long answerId, Long userId, boolean isAdmin);

    boolean updateAnswer(Answer answer, boolean isAdmin);

    Answer getAnswer(Long answerId);

    boolean updateAttitude(Attitude attitude);

    boolean deleteAttitude(Long answerId, Long userId);

    List<AnswerInfo> queryAnswers(Long questionId, Long userId);

    AnswerInfo getAnswerInfo(Long answerId, Long userId);

    boolean getAgree(Long answerId, Long userId);

    List<QuestionInfo> queryAnswersByUserId(Long peopleId, Long userId);

    Long getTopAnswerId(Long questionId);
}
