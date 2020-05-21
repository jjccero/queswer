package com.gzu.queswer.model.vo;

import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.UserApi;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerInfo extends UserApi {
    private Answer answer;
    private Boolean attituded;
    private Long agree;
    private Long against;
    private Long reviewCount;
}
