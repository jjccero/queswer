package com.gzu.queswer.service;

import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.info.QuestionInfo;

import java.util.List;

public interface QuestionService {
    Long saveQuestion(Question question);

    QuestionInfo getQuestionInfo(Long qId, Long aId, Long uId, boolean userAnswer, boolean inc);

    QuestionInfo getQuestionInfo(Long qId, Long uId, boolean inc);

    List<QuestionInfo> queryQuestions(int offset, int count, Long uId);

    boolean saveSubscribe(Long qId, Long uId);

    boolean deleteSubscribe(Long qId, Long uId);

    Question getQuestionByQId(Long qId);
}
