package com.gzu.queswer.service;

import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.info.QuestionInfo;

import java.util.List;

public interface QuestionService {
    Long saveQuestion(Question question);

    QuestionInfo getQuestionInfo(Long questionId, Long answerId, Long userId, boolean userAnswer, boolean inc);

    QuestionInfo getQuestionInfo(Long questionId, Long userId, boolean inc);

    List<QuestionInfo> queryQuestions(int offset, int limit, Long userId);

    boolean saveSubscribe(Long questionId, Long userId);

    boolean deleteSubscribe(Long questionId, Long userId);

    Question getQuestionByQId(Long questionId);
}
