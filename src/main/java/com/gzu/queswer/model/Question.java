package com.gzu.queswer.model;

public class Question{
    private Long qid;
    private Long uid;
    private Long question_time;
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

    public Long getQuestion_time() {
        return question_time;
    }

    public void setQuestion_time(Long question_time) {
        this.question_time = question_time;
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

}
