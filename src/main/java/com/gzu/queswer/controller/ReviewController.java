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

    @PostMapping("/insertReview")
    public Long insertReview(@RequestBody Review review) {
        review.setGmtCreate(DateUtil.getUnixTime());
        return reviewService.insertReview(review);
    }

    @GetMapping("/deleteReview")
    public boolean deleteReview(Long rid, Long uid) {
        return reviewService.deleteReview(rid, uid);
    }

    @GetMapping("/getReviews")
    public List getReviews(Long aid, Long uid) {
        return reviewService.getReviews(aid, uid);
    }

    @GetMapping("/updateApprove")
    public boolean updateApprove(Long rid, Long uid,Boolean approve){
        if(rid==null||uid==null||approve==null) return false;
        return reviewService.updateApprove(rid, uid,approve);
    }

}
