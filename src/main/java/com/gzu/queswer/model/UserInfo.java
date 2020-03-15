package com.gzu.queswer.model;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private Long uid;
    private String nickname;
    private Boolean anonymous;
    private String intro;
    private Boolean avater;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public final static UserInfo defaultUserInfo;

    static {
        defaultUserInfo = new UserInfo();
        defaultUserInfo.setNickname("匿名用户");
        defaultUserInfo.setIntro("猜猜我是谁");
        defaultUserInfo.setUid(null);
        defaultUserInfo.setAnonymous(true);
        defaultUserInfo.setAvater(null);
    }

    public Boolean getAvater() {
        return avater;
    }

    public void setAvater(Boolean avater) {
        this.avater = avater;
    }
}
