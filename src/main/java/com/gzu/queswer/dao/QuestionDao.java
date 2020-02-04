package com.gzu.queswer.dao;

import com.gzu.queswer.model.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao {
    Question selectQuestionByQid(@Param("qid") Long qid);

    void insertQuestion(Question question);

    List selectQuestions(@Param("offset") int offset, @Param("limit") int limit);

    Integer selectFollowCount(@Param("qid") Long qid);

    Integer insertFollow(@Param("qid") Long qid, @Param("uid") Long uid);

    Integer deleteFollow(@Param("qid") Long qid, @Param("uid") Long uid);

    Boolean isFollowed(@Param("qid") Long qid, @Param("uid") Long uid);

    Boolean isQuestioned(@Param("qid") Long qid, @Param("uid") Long uid);

    List selectFollowsByUid(@Param("uid") Long uid);
}
