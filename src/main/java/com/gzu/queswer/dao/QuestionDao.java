package com.gzu.queswer.dao;

import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao {
    Question selectQuestionByQid(@Param("qid") Long qid);

    void insertQuestion(Question question);

    @Deprecated
    List selectQuestions(@Param("offset") int offset, @Param("limit") int limit);

    Integer selectFollowCount(@Param("qid") Long qid);

    Integer insertFollow(@Param("qid") Long qid, @Param("uid") Long uid);

    Integer deleteFollow(@Param("qid") Long qid, @Param("uid") Long uid);

    @Deprecated
    Boolean isFollowed(@Param("qid") Long qid, @Param("uid") Long uid);

    @Deprecated
    Boolean isQuestioned(@Param("qid") Long qid, @Param("uid") Long uid);

    List selectFollowsByUid(@Param("uid") Long uid);

    Answer selectAnswerByUid(@Param("qid") Long qid, @Param("uid") Long uid);

    @Deprecated
    List selectAidsByQid(Long qid);
}
