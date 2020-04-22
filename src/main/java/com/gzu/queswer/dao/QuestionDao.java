package com.gzu.queswer.dao;

import com.gzu.queswer.model.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionDao {
    Question selectQuestion(@Param("questionId") Long questionId);

    void insertQuestion(Question question);

    Long selectAnswerIdByUserId(@Param("questionId") Long questionId, @Param("userId") Long userId);
}
