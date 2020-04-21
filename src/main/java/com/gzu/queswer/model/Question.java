package com.gzu.queswer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Question implements Serializable {
    private Long questionId;
    private Long userId;
    private Long gmtCreate;
    private Long gmtModify;
    private String title;
    private String detail;
    private Boolean anonymous;
}
