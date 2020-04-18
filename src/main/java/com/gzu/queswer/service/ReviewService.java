package com.gzu.queswer.service;

import com.gzu.queswer.dao.impl.ReviewServiceImpl;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.model.info.ReviewInfo;
import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewServiceImpl reviewDaoImpl;
    @Autowired
    UserService userService;
    @Autowired
    AnswerService answerService;
    @Autowired
    QuestionService questionService;

    public Long saveReview(Review review) {
        review.setrId(null);
        review.setGmtCreate(DateUtil.getUnixTime());
        return reviewDaoImpl.insertReview(review);
    }

    public boolean deleteReview(Long rid, Long uid) {
        return reviewDaoImpl.deleteReviewByUid(rid, uid);
    }

    public List<ReviewInfo> queryReviews(Long aid, Long uid) {
        List<Long> rIds = answerService.selectRidsByAid(aid);
        List<ReviewInfo> reviewInfos = new ArrayList<>(rIds.size());
        Answer answer = answerService.selectAnswerByAid(aid);
        Question question = questionService.getQuestionByQid(answer.getqId());
        Long answerUId = answer.getuId();
        Long questionUId = question.getuId();
        Boolean answererAnonymous = answer.getAnonymous();
        Boolean questionerAnonymous = question.getAnonymous();
        for (Long rid : rIds) {
            ReviewInfo reviewInfo = reviewDaoImpl.getReviewInfo(rid, uid);
            Review review = reviewInfo.getReview();
            Long review_uid = review.getuId();
            reviewInfo.setAnonymous(false);
            if (questionUId.equals(review_uid)) {
                reviewInfo.setQuestioned(true);
                reviewInfo.setAnonymous(questionerAnonymous);
            } else reviewInfo.setQuestioned(false);
            if (answerUId.equals(review_uid)) {
                reviewInfo.setQuestioned(false);
                reviewInfo.setAnswered(true);
                reviewInfo.setAnonymous(answererAnonymous);
            } else reviewInfo.setAnswered(false);
            setUserInfo(reviewInfo, uid);
            reviewInfos.add(reviewInfo);
        }
        return reviewInfos;
    }

    public void setUserInfo(ReviewInfo reviewInfo, Long uid) {
        Review review = reviewInfo.getReview();
        UserInfo userInfo;
        Boolean anonymous = reviewInfo.getAnonymous();
        if (Boolean.TRUE.equals(anonymous) && !review.getuId().equals(uid)) {
            userInfo = UserInfo.defaultUserInfo;
            review.setuId(null);
        } else {
            userInfo = userService.getUserInfo(review.getuId(), uid);
            userInfo.setAnonymous(anonymous);
        }
        reviewInfo.setUserInfo(userInfo);
    }

    public boolean updateApprove(Long rid, Long uid, Boolean approve) {
        return reviewDaoImpl.updateApprove(rid, uid, approve);
    }

}
