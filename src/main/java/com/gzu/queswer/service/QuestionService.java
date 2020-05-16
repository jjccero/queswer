package com.gzu.queswer.service;

import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.vo.QuestionInfo;

import java.util.List;

public interface QuestionService {
    Long saveQuestion(Question question);

    boolean updateQuestion(Question question);

    QuestionInfo getQuestionInfo(Long questionId, Long answerId, Long userId, boolean userAnswer, boolean inc);

    QuestionInfo getQuestionInfo(Long questionId, Long userId, boolean inc);

    List<QuestionInfo> queryQuestions(int page, int limit, Long userId);

    boolean saveSubscribe(Long questionId, Long userId);

    boolean deleteSubscribe(Long questionId, Long userId);

    Question getQuestion(Long questionId);

    List<QuestionInfo> queryQuestionsByUserId(Long peopleId, Long userId);
}
