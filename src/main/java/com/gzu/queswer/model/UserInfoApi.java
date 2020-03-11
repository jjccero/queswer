package com.gzu.queswer.model;

import java.io.Serializable;

public class UserInfoApi implements Serializable {
    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

}
