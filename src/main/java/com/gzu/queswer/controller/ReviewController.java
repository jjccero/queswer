package com.gzu.queswer.controller;

import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.info.ReviewInfo;
import com.gzu.queswer.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {
    @Autowired
    ReviewService reviewService;

    @PostMapping("/saveReview")
    public Long saveReview(@RequestBody Review review) {
        return reviewService.saveReview(review);
    }

    @GetMapping("/deleteReview")
    public boolean deleteReview(Long reviewId, Long userId) {
        return reviewService.deleteReview(reviewId, userId);
    }

    @GetMapping("/queryReviews")
    public List<ReviewInfo> queryReviews(Long answerId, Long userId) {
        return reviewService.queryReviews(answerId, userId);
    }

    @GetMapping("/updateApprove")
    public boolean updateApprove(Long reviewId, Long userId, Boolean approve) {
        return reviewService.updateApprove(reviewId, userId, approve);
    }

}
