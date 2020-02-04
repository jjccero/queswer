package com.gzu.queswer.service;

import com.gzu.queswer.dao.ReviewDao;
import com.gzu.queswer.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReviewService {
    @Autowired
    private ReviewDao reviewDao;

    public Long addReview(Review review) {
        reviewDao.insertReview(review);
        return review.getRid();
    }

    public Integer deleteReviewSuper(Long rid) {
        return reviewDao.deleteReviewByRid(rid);
    }

    public Integer deleteReview(Long rid, Long uid) {
        return reviewDao.deleteReviewByUid(rid, uid);
    }

    public List getReviewList(Long aid) {
        return reviewDao.selectReviewsByAid(aid);
    }

    public Map selectAnswererByAid(Long aid){
        return reviewDao.selectAnswererByAid(aid);
    }

    public Map selectQuestionerByQid(Long qid){
        return reviewDao.selectQuestionerByQid(qid);
    }
}
