package com.gzu.queswer.controller;

import com.gzu.queswer.model.Review;
import com.gzu.queswer.service.ReviewService;
import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @RequestMapping("addReview")
    public Long addReview(@RequestBody Review review) {
        review.setReview_time(DateUtil.getUnixTime());
        return reviewService.insertReview(review);
    }

    @RequestMapping("deleteReviewSuper")
    public Integer deleteReviewSuper(Long rid) {
        return reviewService.deleteReviewSuper(rid);
    }

    @RequestMapping("deleteReview")
    public boolean deleteReview(Long rid, Long uid) {
        return reviewService.deleteReview(rid, uid);
    }

    @RequestMapping("getReviews")
    public List getReviews(Long aid, Long uid) {
        return reviewService.getReviews(aid, uid);
    }

    @RequestMapping("updateApprove")
    public boolean updateApprove(Long rid, Long uid,Boolean approve){
        if(rid==null||uid==null||approve==null) return false;
        return reviewService.updateApprove(rid, uid,approve);
    }

}