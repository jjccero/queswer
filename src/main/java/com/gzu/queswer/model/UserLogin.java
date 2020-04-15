package com.gzu.queswer.model;

public class UserLogin {
    private Long uId;
    private String username;
    private String password;
    private Long gmtCreate;
    private Short authority;

    public Long getuId() {
        return uId;
    }

    public void setuId(Long uId) {
        this.uId = uId;
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

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Short getAuthority() {
        return authority;
    }

    private void setAuthority(Short authority) {
        this.authority = authority;
    }

    public void setSuperUser() {
        setAuthority(SUPER_USER);
    }

    public void setNormalUser() {
        setAuthority(NORMAL_USER);
    }

    private static final Short SUPER_USER = (short) 1;
    private static final Short NORMAL_USER = (short) 0;
}
