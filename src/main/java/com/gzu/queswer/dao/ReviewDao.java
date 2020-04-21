package com.gzu.queswer.dao;

import com.gzu.queswer.model.Review;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewDao {
    void insertReview(Review review);

    int deleteReview(Long rId);

    Review selectReview(Long rId);
}
