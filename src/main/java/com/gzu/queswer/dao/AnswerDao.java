package com.gzu.queswer.dao;

import com.gzu.queswer.model.Answer;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerDao {
    void insertAnswer(Answer answer);

    int deleteAnswer(Long answerId);

    int updateAnswer(Answer answer);

    Answer selectAnswer(Long answerId);

}
