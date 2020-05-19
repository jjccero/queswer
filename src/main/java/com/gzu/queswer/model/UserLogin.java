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

    public void setAdmin() {
        setAuthority(ADMIN);
    }

    public void setNormalUser() {
        setAuthority(NORMAL_USER);
    }

    public static final Short SUPER_ADMIN = (short) 2;
    public static final Short ADMIN = (short) 1;
    public static final Short NORMAL_USER = (short) 0;
}
