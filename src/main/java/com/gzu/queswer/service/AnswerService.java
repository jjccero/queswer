package com.gzu.queswer.service;

import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.info.AnswerInfo;

import java.util.List;

public interface AnswerService {
    Long saveAnswer(Answer answer);

    boolean deleteAnswer(Long aId, Long uId);

    boolean updateAnswer(Answer answer);

    boolean updateAttitude(Attitude attitude);

    boolean deleteAttitude(Long aId, Long uId);

    List<AnswerInfo> queryAnswers(Long qId, Long uId);

    AnswerInfo getAnswerInfo(Long aId, Long uId);

    Answer selectAnswerByAid(Long aId);

    List<Long> selectRidsByAid(Long aId);

}
