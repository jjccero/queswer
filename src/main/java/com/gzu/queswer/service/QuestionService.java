package com.gzu.queswer.service;

import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.vo.ActivityInfo;
import com.gzu.queswer.model.vo.QuestionInfo;

import java.util.List;

public interface QuestionService {
    Long saveQuestion(Question question);

    boolean deleteQuestion(Long questionId);

    boolean updateQuestion(Question question);

    Question getQuestion(Long questionId);

    QuestionInfo getQuestionInfo(Long questionId, Long answerId, Long userId, boolean userAnswer, boolean inc);

    QuestionInfo getQuestionInfo(Long questionId, Long userId, boolean inc);

    List<QuestionInfo> queryQuestions(int page, int limit, Long userId);

    boolean saveSubscribeQuestion(Long questionId, Long userId);

    boolean deleteSubscribeQuestion(Long questionId, Long userId);

    List<QuestionInfo> queryQuestionsByUserId(Long peopleId, Long userId);

    List<ActivityInfo> querySubscribeQuestionsByUserId(Long peopleId, Long userId);
}
