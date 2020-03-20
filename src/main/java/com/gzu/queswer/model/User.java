package com.gzu.queswer.model;

public class User {
    private Long uid;
    private Long gmt_create;
    private Short authority;
    private String nickname;
    private Short sex;
    private Boolean avater;
    private String intro;

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }


    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public void setAuthority(Short authority) {
        this.authority = authority;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Short getSex() {
        return sex;
    }

    public void setSex(Short sex) {
        this.sex = sex;
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

    public Boolean getAvater() {
        return avater;
    }

    public void setAvater(Boolean avater) {
        this.avater = avater;
    }


}
