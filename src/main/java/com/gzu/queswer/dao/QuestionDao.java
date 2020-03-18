package com.gzu.queswer.dao;

import com.gzu.queswer.model.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao {
    Question selectQuestionByQid(@Param("qid") Long qid);

    void insertQuestion(Question question);

    Integer selectFollowCount(@Param("qid") Long qid);

    Integer insertFollow(@Param("qid") Long qid, @Param("uid") Long uid);

    Integer deleteFollow(@Param("qid") Long qid, @Param("uid") Long uid);

    List selectFollowsByUid(@Param("uid") Long uid);

    Long selectAidByUid(@Param("qid") Long qid, @Param("uid") Long uid);

}
