package com.gzu.queswer.dao;

import com.gzu.queswer.model.Answer;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerDao {

    void insertAnswer(Answer answer);

    Integer deleteAnswer(@Param("aid") Long aid, @Param("uid") Long uid);

    Integer updateAnswer(Answer answer);

    Integer insertAttitude(@Param("aid") Long aid, @Param("uid") Long uid, @Param("attitude") Boolean attitude);

    Integer deleteAttitude(@Param("aid") Long aid, @Param("uid") Long uid);

    List selectAttitudeByAid(Long aid);

    Boolean selectAttitudeByUid(@Param("aid") Long aid, @Param("uid") Long uid);

    List selectAnswersByQid(@Param("qid") Long qid);

    Integer selectAnswerCountByAid(@Param("qid") Long qid);

    Answer selectAnswerByUid(@Param("qid") Long qid, @Param("uid") Long uid);
}
