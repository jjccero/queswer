package com.gzu.queswer.model;

public class Answer {
    private Long aid;
    private Long uid;
    private Long qid;
    private Long answer_time;
    private Long modify_answer_time;
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

    public Long getAnswer_time() {
        return answer_time;
    }

    public void setAnswer_time(Long answer_time) {
        this.answer_time = answer_time;
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

    public Long getModify_answer_time() {
        return modify_answer_time;
    }

    public void setModify_answer_time(Long modify_answer_time) {
        this.modify_answer_time = modify_answer_time;
    }
}
