package com.gzu.queswer.controller;

import com.gzu.queswer.model.Review;
import com.gzu.queswer.service.ReviewService;
import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/saveReview")
    public Long insertReview(@RequestBody Review review) {
        review.setGmtCreate(DateUtil.getUnixTime());
        return reviewService.insertReview(review);
    }

    @GetMapping("/deleteReview")
    public boolean deleteReview(Long rId, Long uId) {
        return reviewService.deleteReview(rId, uId);
    }

    @GetMapping("/queryReviews")
    public List getReviews(Long aId, Long uId) {
        return reviewService.getReviews(aId, uId);
    }

    @GetMapping("/updateApprove")
    public boolean updateApprove(Long rId, Long uId,Boolean approve){
        if(rId==null||uId==null||approve==null) return false;
        return reviewService.updateApprove(rId, uId,approve);
    }

}
