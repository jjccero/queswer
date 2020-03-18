package com.gzu.queswer.dao;

import com.gzu.queswer.model.Review;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewDao {

    void insertReview(Review review);

    Integer deleteReviewByRid(@Param("rid") Long rid);

    List selectRidsByAid(@Param("aid") Long aid);

    Review selectReviewByRid(Long rid);
}
