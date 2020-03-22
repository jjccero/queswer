package com.gzu.queswer.model;

import java.io.Serializable;

public class User implements Serializable {
    private Long uid;
    private String nickname;
    private String intro;
    private Boolean avater;
    private Long gmt_create;
    private Short authority;
    private Short sex;


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

    public Long getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(Long gmt_create) {
        this.gmt_create = gmt_create;
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
