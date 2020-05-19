package com.gzu.queswer.service;

import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.vo.ReviewInfo;

import java.util.List;

public interface ReviewService {
    Long saveReview(Review review);

    boolean deleteReview(Long rId, Long userId,boolean isAdmin);

    List<ReviewInfo> queryReviews(Long answerId, Long userId);

    boolean updateApprove(Long rId, Long userId, Boolean approve);

}
