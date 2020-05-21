package com.gzu.queswer.model.vo;

import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.UserApi;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuestionInfo extends UserApi {
    private Question question;
    private Long viewCount;
    private Long subscribeCount;
    private Boolean subscribed;
    private Boolean questioned;
    private AnswerInfo defaultAnswer;
    private AnswerInfo userAnswer;
    private Long answerCount;
}
