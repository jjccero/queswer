package com.gzu.queswer.model.info;

import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.Topic;
import com.gzu.queswer.model.UserApi;

import java.util.List;

public class QuestionInfo extends UserApi {
    private Question question;
    private Double viewCount;
    private Long subscribeCount;
    private Boolean subscribed;
    private Boolean questioned;
    private AnswerInfo defaultAnswer;
    private AnswerInfo userAnswer;
    private List<Topic> topics;
    private Long answerCount;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Double getViewCount() {
        return viewCount;
    }

    public void setViewCount(Double viewCount) {
        this.viewCount = viewCount;
    }

    public Long getSubscribeCount() {
        return subscribeCount;
    }

    public void setSubscribeCount(Long subscribeCount) {
        this.subscribeCount = subscribeCount;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    public Boolean getQuestioned() {
        return questioned;
    }

    public void setQuestioned(Boolean questioned) {
        this.questioned = questioned;
    }

    public AnswerInfo getDefaultAnswer() {
        return defaultAnswer;
    }

    public void setDefaultAnswer(AnswerInfo defaultAnswer) {
        this.defaultAnswer = defaultAnswer;
    }

    public AnswerInfo getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(AnswerInfo userAnswer) {
        this.userAnswer = userAnswer;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public Long getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Long answerCount) {
        this.answerCount = answerCount;
    }
}
