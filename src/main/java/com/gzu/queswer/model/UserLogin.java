package com.gzu.queswer.model;

public class UserLogin {
    private Long uid;
    private String username;
    private String password;
    private Long gmt_create;
    private Short authority;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setSuperUser() {
        setAuthority(superUser);
    }

    public void setNormalUser() {
        setAuthority(normalUser);
    }

    public final static Short superUser = new Short((short) 1);
    public final static Short normalUser = new Short((short) 0);
}
