package com.gzu.queswer.model;

import java.io.Serializable;

public class Review implements Serializable {
    private Long rId;
    private Long replyRId;
    private Long gmtCreate;
    private Long aId;
    private String revi;
    private Boolean deleted;
    private Long uId;

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getuId() {
        return uId;
    }

    public void setuId(Long uId) {
        this.uId = uId;
    }

    public Long getrId() {
        return rId;
    }

    public void setrId(Long rId) {
        this.rId = rId;
    }

    public Long getReplyRId() {
        return replyRId;
    }

    public void setReplyRId(Long replyRId) {
        this.replyRId = replyRId;
    }

    public Long getaId() {
        return aId;
    }

    public void setaId(Long aId) {
        this.aId = aId;
    }

    public String getReview() {
        return revi;
    }

    public void setReview(String review) {
        this.revi = review;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }


}
