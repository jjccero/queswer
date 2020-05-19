package com.gzu.queswer.controller;

import com.gzu.queswer.common.UserContext;
import com.gzu.queswer.common.UserException;
import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.vo.ReviewInfo;
import com.gzu.queswer.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {
    @Autowired
    ReviewService reviewService;
    @Autowired
    UserContext userContext;

    @PostMapping("/saveReview")
    public Long saveReview(@RequestBody Review review) throws UserException {
        review.setUserId(userContext.getUserId(true));
        return reviewService.saveReview(review);
    }

    @GetMapping("/deleteReview")
    public boolean deleteReview(Long reviewId) throws UserException {
        return reviewService.deleteReview(reviewId, userContext.getUserId(true), userContext.check(UserLogin.ADMIN, false));
    }

    @GetMapping("/queryReviews")
    public List<ReviewInfo> queryReviews(Long answerId) throws UserException {
        return reviewService.queryReviews(answerId, userContext.getUserId(false));
    }

    @GetMapping("/updateApprove")
    public boolean updateApprove(Long reviewId, Boolean approve) throws UserException {
        return reviewService.updateApprove(reviewId, userContext.getUserId(true), approve);
    }

}
