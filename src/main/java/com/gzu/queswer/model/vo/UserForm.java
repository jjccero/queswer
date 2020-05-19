package com.gzu.queswer.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UserForm implements Serializable {
    private Long userId;
    private String nickname;
    private String intro;
    private Short sex;
    private String email;
}
