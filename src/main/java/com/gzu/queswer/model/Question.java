package com.gzu.queswer.model;

public class Question {
    private Long qid;
    private Long uid;
    private Long gmt_create;
    private Long gmt_modify;
    private String question;
    private String detail;
    private Boolean anonymous;

    public Long getQid() {
        return qid;
    }

    public void setQid(Long qid) {
        this.qid = qid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Long getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(Long gmt_create) {
        this.gmt_create = gmt_create;
    }

    public Long getGmt_modify() {
        return gmt_modify;
    }

    public void setGmt_modify(Long gmt_modify) {
        this.gmt_modify = gmt_modify;
    }
}
