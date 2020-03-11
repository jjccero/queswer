package com.gzu.queswer.service;

import com.gzu.queswer.dao.ReviewDaoImpl;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.UserInfo;
import com.gzu.queswer.model.info.ReviewInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewDaoImpl reviewDaoImpl;
    @Autowired
    UserService userService;
    @Autowired
    AnswerService answerService;
    @Autowired
    QuestionService questionService;

    public Long addReview(Review review) {
        reviewDaoImpl.insertReview(review);
        return answerService.addReview(review);
    }

    public Integer deleteReviewSuper(Long rid) {
        return reviewDaoImpl.deleteReviewByRid(rid);
    }

    public boolean deleteReview(Long rid, Long uid) {
        return reviewDaoImpl.deleteReviewByUid(rid, uid);
    }

    public List getReviews(Long aid, Long uid) {
        List<Long> rids = answerService.selectRidsByAid(aid);
        List<ReviewInfo> reviewInfos = new ArrayList<>();
        Answer answer = answerService.selectAnswerByAid(aid);
        Question question = questionService.selectQuestionByQid(answer.getQid());
        Long answer_uid = answer.getUid();
        Long question_uid = question.getUid();
        Boolean answerer_anonymous = answer.getAnonymous();
        Boolean questioner_anonymous = question.getAnonymous();
        for (Long rid : rids) {
            ReviewInfo reviewInfo = reviewDaoImpl.getReviewInfo(rid, uid);
            Review review = reviewInfo.getReview();
            Long review_uid = review.getUid();
            reviewInfo.setAnonymous(false);
            if (question_uid.equals(review_uid)) {
                reviewInfo.setQuestioned(true);
                reviewInfo.setAnonymous(questioner_anonymous);
            }
            else {
                if (answer_uid.equals(review_uid)) {
                    reviewInfo.setAnonymous(answerer_anonymous);
                    reviewInfo.setAnswered(true);
                }
                reviewInfo.setQuestioned(false);
            }
            setUserInfo(reviewInfo,uid);
            reviewInfos.add(reviewInfo);
        }
        return reviewInfos;
    }

    public void setUserInfo(ReviewInfo reviewInfo, Long uid) {
        Review review = reviewInfo.getReview();
        UserInfo userInfo;
        Boolean anonymous = reviewInfo.getAnonymous();
        if (anonymous && !review.getUid().equals(uid)) {
            userInfo = UserInfo.defaultUserInfo;
            review.setUid(null);
        } else {
            userInfo = userService.getUserInfo(review.getUid());
            userInfo.setAnonymous(anonymous);
        }
        reviewInfo.setUserInfo(userInfo);
    }
}
