package com.gzu.queswer.service;

import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.info.ReviewInfo;

import java.util.List;

public interface ReviewService {
    Long saveReview(Review review);

    boolean deleteReview(Long rId, Long uId);

    List<ReviewInfo> queryReviews(Long aId, Long uId);

    boolean updateApprove(Long rId, Long uId, Boolean approve);

}
