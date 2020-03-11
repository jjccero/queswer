package com.gzu.queswer.model.info;

import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.UserInfoApi;

public class ReviewInfo extends UserInfoApi {
    private Review review;
    private Boolean questioned;
    private Boolean answered;
    private Long approveCount;
    private Boolean approved;
    private Boolean anonymous;

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Boolean getQuestioned() {
        return questioned;
    }

    public void setQuestioned(Boolean questioned) {
        this.questioned = questioned;
    }

    public Boolean getAnswered() {
        return answered;
    }

    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }

    public Long getApproveCount() {
        return approveCount;
    }

    public void setApproveCount(Long approveCount) {
        this.approveCount = approveCount;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }
}
