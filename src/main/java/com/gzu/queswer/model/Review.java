package com.gzu.queswer.model;

public class Review extends UserInfoApi {
    private Long rid;
    private Long reply_rid;
    private Long review_time;
    private Long aid;
    private String review;

    public boolean isQuestioner() {
        return isQuestioner;
    }

    public void setQuestioner(boolean questioner) {
        isQuestioner = questioner;
    }

    private boolean isQuestioner=false;

    public boolean isAnswerer() {
        return isAnswerer;
    }

    public void setAnswerer(boolean answerer) {
        isAnswerer = answerer;
    }

    private boolean isAnswerer=false;
    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public Long getReply_rid() {
        return reply_rid;
    }

    public void setReply_rid(Long reply_rid) {
        this.reply_rid = reply_rid;
    }

    public Long getReview_time() {
        return review_time;
    }

    public void setReview_time(Long review_time) {
        this.review_time = review_time;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    private Boolean deleted;
}
