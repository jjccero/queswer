package com.gzu.queswer.model;

public class User {
    private Long uid;
    private Long signup_time;
    private String username;
    private String password;
    private Short authority;
    private String nickname;
    private Short sex;
    private Boolean avater;

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    private String intro;
    public final static Short superUser = new Short((short) 1);
    public final static Short normalUser = new Short((short) 0);

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getSignup_time() {
        return signup_time;
    }

    public void setSignup_time(Long signup_time) {
        this.signup_time = signup_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Short getAuthority() {
        return authority;
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

    public void setSuperUser() {
        setAuthority(superUser);
    }

    public void setNormalUser() {
        setAuthority(normalUser);
    }

    public Boolean getAvater() {
        return avater;
    }

    public void setAvater(Boolean avater) {
        this.avater = avater;
    }
}
