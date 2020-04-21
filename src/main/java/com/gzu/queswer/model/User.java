package com.gzu.queswer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class User implements Serializable {
    private Long userId;
    private String nickname;
    private String intro;
    private Boolean avater;
    private Long gmtCreate;
    private Short authority;
    private Short sex;
}
