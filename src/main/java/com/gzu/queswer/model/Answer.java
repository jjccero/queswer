package com.gzu.queswer.model;

public class Answer extends UserInfoApi {
    private Long aid;
    private Long qid;
    private Long answer_time;
    private String answer;

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
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

}
