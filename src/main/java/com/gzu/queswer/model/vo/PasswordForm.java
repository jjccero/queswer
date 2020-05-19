package com.gzu.queswer.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class PasswordForm implements Serializable {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}
