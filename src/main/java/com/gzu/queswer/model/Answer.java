package com.gzu.queswer.model;

public class Answer {
    private Long aid;
    private Long uid;
    private Long qid;
    private Long gmt_create;
    private Long gmt_modify;
    private String answer;
    private Boolean anonymous;

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getQid() {
        return qid;
    }

    public void setQid(Long qid) {
        this.qid = qid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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
