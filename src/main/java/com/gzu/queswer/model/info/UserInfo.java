package com.gzu.queswer.model.info;

import com.gzu.queswer.model.User;

public class UserInfo {
    public final static UserInfo defaultUserInfo;
    private User user;

    static {
        User defaultUser = new User();
        defaultUser.setNickname("匿名用户");
        defaultUser.setIntro("猜猜我是谁");
        defaultUser.setUid(null);
        defaultUser.setAvater(null);
        defaultUserInfo=new UserInfo();
        defaultUserInfo.setUser(defaultUser);
        defaultUserInfo.setAnonymous(true);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    private Boolean anonymous;
    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }
}
