package com.gzu.queswer.dao;

import com.gzu.queswer.model.Review;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReviewDao {

    void insertReview(Review review);

    Integer deleteReviewByRid(@Param("rid") Long rid);

    @Deprecated
    Integer deleteReviewByUid(@Param("rid") Long rid, @Param("uid") Long uid);

    List selectRidsByAid(@Param("aid") Long aid);

    @Deprecated
    Map selectAnswererByAid(@Param("aid") Long aid);

    @Deprecated
    Map selectQuestionerByQid(@Param("qid") Long qid);

    Review selectReviewByRid(Long rid);
}
