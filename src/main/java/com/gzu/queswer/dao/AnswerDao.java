package com.gzu.queswer.dao;

import com.gzu.queswer.model.Answer;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerDao {
    void insertAnswer(Answer answer);

    int deleteAnswer(Long aId);

    int updateAnswer(Answer answer);

    Answer selectAnswerbyAId(Long aId);


}
