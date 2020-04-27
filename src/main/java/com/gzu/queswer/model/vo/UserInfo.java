package com.gzu.queswer.model.vo;

import com.gzu.queswer.model.User;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private User user;
    private Boolean anonymous;
    private Boolean followed;
    private Long followersCount;
    private Long followCount;

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

    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }

    public Long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Long followersCount) {
        this.followersCount = followersCount;
    }

    public Long getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Long followCount) {
        this.followCount = followCount;
    }

    public static UserInfo getDefaultUserInfo() {
        return defaultUserInfo;
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
