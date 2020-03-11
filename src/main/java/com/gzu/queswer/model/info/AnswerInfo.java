package com.gzu.queswer.model.info;

import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.UserInfoApi;

public class AnswerInfo extends UserInfoApi {
    private Answer answer;
    private Boolean attituded;
    private Long agree;
    private Long against;

    public Boolean getAttituded() {
        return attituded;
    }

    public void setAttituded(Boolean attituded) {
        this.attituded = attituded;
    }

    public Long getAgree() {
        return agree;
    }

    public void setAgree(Long agree) {
        this.agree = agree;
    }

    public Long getAgainst() {
        return against;
    }

    public void setAgainst(Long against) {
        this.against = against;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
