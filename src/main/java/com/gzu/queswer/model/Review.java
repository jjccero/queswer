package com.gzu.queswer.model;

public class Review {
    private Long rid;
    private Long reply_rid;
    private Long gmt_create;
    private Long aid;
    private String review;
    private Boolean deleted;
    private Long uid;

    public Long getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(Long gmt_create) {
        this.gmt_create = gmt_create;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

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


}
