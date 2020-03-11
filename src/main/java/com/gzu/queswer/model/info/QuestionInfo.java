package com.gzu.queswer.model.info;

import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.Topic;
import com.gzu.queswer.model.UserInfoApi;

import java.util.List;

public class QuestionInfo extends UserInfoApi {
    private Question question;
    private Double viewCount;
    private Long followCount;
    private Boolean followed;
    private Boolean questioned;
    private Answer answer;
    private List<Topic> topics;

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

    public Long getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Long followCount) {
        this.followCount = followCount;
    }

    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }

    public Boolean getQuestioned() {
        return questioned;
    }

    public void setQuestioned(Boolean questioned) {
        this.questioned = questioned;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}
