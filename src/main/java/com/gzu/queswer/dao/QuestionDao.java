package com.gzu.queswer.dao;

import com.gzu.queswer.model.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao {
    Question selectQuestionByQid(@Param("qId") Long qId);

    void insertQuestion(Question question);

    Integer selectFollowCount(@Param("qId") Long qId);

    int insertFollow(@Param("qId") Long qId, @Param("uId") Long uId);

    int deleteFollow(@Param("qId") Long qId, @Param("uId") Long uId);

    List selectFollowsByUid(@Param("uId") Long uId);

    List<Long> selectAIdsByQId(Long qId);

    List<Long> selectQIds();

    Long selectAidByUid(@Param("qId") Long qId, @Param("uId") Long uId);

}
