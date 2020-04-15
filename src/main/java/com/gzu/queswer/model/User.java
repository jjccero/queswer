package com.gzu.queswer.model;

import java.io.Serializable;

public class User implements Serializable {
    private Long uId;
    private String nickname;
    private String intro;
    private Boolean avater;
    private Long gmtCreate;
    private Short authority;
    private Short sex;


    public Long getuId() {
        return uId;
    }

    public void setuId(Long uId) {
        this.uId = uId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Boolean getAvater() {
        return avater;
    }

    public void setAvater(Boolean avater) {
        this.avater = avater;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Short getAuthority() {
        return authority;
    }

    public void setAuthority(Short authority) {
        this.authority = authority;
    }

    public Short getSex() {
        return sex;
    }

    public void setSex(Short sex) {
        this.sex = sex;
    }

}
