package com.gzu.queswer.dao;

import com.gzu.queswer.model.Answer;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerDao {

    void insertAnswer(Answer answer);

    Integer deleteAnswerByAid(Long aId);

    Integer updateAnswer(Answer answer);

    Answer selectAnswerbyAid(Long aId);


}
