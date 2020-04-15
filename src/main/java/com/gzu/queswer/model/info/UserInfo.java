package com.gzu.queswer.model.info;

import com.gzu.queswer.model.User;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private User user;
    private Boolean anonymous;
    private Long supportCount;

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

    public Long getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(Long supportCount) {
        this.supportCount = supportCount;
    }

    public static final UserInfo defaultUserInfo;

    static {
        User defaultUser = new User();
        defaultUser.setNickname("匿名用户");
        defaultUser.setIntro("猜猜我是谁");
        defaultUser.setuId(null);
        defaultUser.setAvater(null);
        defaultUserInfo = new UserInfo();
        defaultUserInfo.setUser(defaultUser);
        defaultUserInfo.setAnonymous(true);
    }
}
