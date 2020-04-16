package com.gzu.queswer.dao;

import com.gzu.queswer.model.Review;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewDao {

    void insertReview(Review review);

    int deleteReviewByRid(Long rId);

    List selectRidsByAid(Long aId);

    Review selectReviewByRid(Long rId);
}
