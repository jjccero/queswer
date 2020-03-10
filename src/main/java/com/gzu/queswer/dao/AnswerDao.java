package com.gzu.queswer.dao;

import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerDao {

    void insertAnswer(Answer answer);

    Integer deleteAnswer(@Param("aid") Long aid, @Param("uid") Long uid);

    Integer updateAnswer(Answer answer);

    @Deprecated
    Integer insertAttitude(Attitude attitude);

    @Deprecated
    Integer deleteAttitude(@Param("aid") Long aid, @Param("uid") Long uid);

    @Deprecated
    List selectAttitudesByAid(Long aid);

    @Deprecated
    Boolean selectAttitudeByUid(@Param("aid") Long aid, @Param("uid") Long uid);

    @Deprecated
    List selectAnswersByQid(@Param("qid") Long qid);

    Answer selectAnswerByUid(@Param("qid") Long qid, @Param("uid") Long uid);

    Answer selectAnswerbyAid(Long aid);

    List<Long> selectAidsByQid(Long qid);
}
