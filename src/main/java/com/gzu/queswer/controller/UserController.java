package com.gzu.queswer.controller;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 蒋竟成
 * @date 2020/4/19
 */
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping(value = "/login")
    public User login(@RequestBody JSONObject loginForm) {
        String username = loginForm.getString("username");
        String password = loginForm.getString("password");
        return userService.login(username, password);
    }

    @PostMapping(value = "/signup")
    public Long signup(@RequestBody UserLogin userLogin) {
        userLogin.setNormalUser();
        return userService.saveUser(userLogin);
    }

    @PostMapping("/signupSuper")
    public Long signupSuper(@RequestBody UserLogin userLogin) {
        userLogin.setSuperUser();
        return userService.saveUser(userLogin);
    }

    @PostMapping("/updateUser")
    public Integer updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping(value = "/getUserInfo")
    public UserInfo getUserInfo(Long peopleId, Long userId) {
        return userService.getUserInfo(peopleId, userId);
    }

    @GetMapping("/saveFollow")
    public boolean saveFollow(Long userId, Long followerId) {
        return userService.saveFollow(userId, followerId);
    }

    @GetMapping("/deleteFollow")
    public boolean deleteFollow(Long userId, Long followerId) {
        return userService.deleteFollow(userId, followerId);
    }

    @GetMapping("/queryUserInfosByFollowerId")
    public List<UserInfo> queryUserInfosByFollowerId(Long followerId, Long selfId) {
        return userService.queryUserInfosByFollowerId(followerId, selfId);
    }

    @GetMapping("/queryFollowerInfosIdsByUId")
    public List<UserInfo> queryFollowerInfosIdsByUId(Long uId, Long selfId) {
        return userService.queryFollowerInfosIdsByUId(uId, selfId);
    }
}
