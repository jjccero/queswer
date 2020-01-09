package com.gzu.queswer.service;

public interface IUserService {

    /**
     * 注册
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public Integer signup(String username, String password);

    /**
     * 登陆
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public Integer login(String username, String password);

    /**
     * 发送信息
     *
     * @param username1 源用户名
     * @param username2 目的用户名
     * @param message   消息内容
     * @return
     */
    public Integer sendMessage(String username1, String username2, String message);


}
