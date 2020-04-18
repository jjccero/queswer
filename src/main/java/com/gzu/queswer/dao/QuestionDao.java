package com.gzu.queswer.dao;

import com.gzu.queswer.model.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionDao {
    Question selectQuestionByQid(@Param("qId") Long qId);

    void insertQuestion(Question question);

    Long selectAidByUid(@Param("qId") Long qId, @Param("uId") Long uId);

}
