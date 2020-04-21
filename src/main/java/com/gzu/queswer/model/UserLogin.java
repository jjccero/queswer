package com.gzu.queswer.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLogin {
    private Long userId;
    private String username;
    private String password;
    private Long gmtCreate;
    private Short authority;

    public void setSuperUser() {
        setAuthority(SUPER_USER);
    }

    public void setNormalUser() {
        setAuthority(NORMAL_USER);
    }

    private static final Short SUPER_USER = (short) 1;
    private static final Short NORMAL_USER = (short) 0;
}
