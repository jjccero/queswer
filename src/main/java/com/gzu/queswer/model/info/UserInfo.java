package com.gzu.queswer.model.info;

import com.gzu.queswer.model.User;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private User user;
    private Boolean anonymous;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public static final UserInfo defaultUserInfo;

    static {
        User defaultUser = new User();
        defaultUser.setNickname("匿名用户");
        defaultUser.setIntro("猜猜我是谁");
        defaultUser.setUserId(null);
        defaultUser.setAvater(null);
        defaultUserInfo = new UserInfo();
        defaultUserInfo.setUser(defaultUser);
        defaultUserInfo.setAnonymous(true);
    }
}
