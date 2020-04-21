package com.gzu.queswer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Answer implements Serializable {
    private Long answerId;
    private Long userId;
    private Long questionId;
    private Long gmtCreate;
    private Long gmtModify;
    private String ans;
    private Boolean anonymous;
}
