package com.gzu.queswer.model;

import com.gzu.queswer.model.vo.UserInfo;

import java.io.Serializable;

public class UserApi implements Serializable {
    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

}
