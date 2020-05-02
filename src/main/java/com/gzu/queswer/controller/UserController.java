package com.gzu.queswer.controller;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.common.UserContext;
import com.gzu.queswer.common.UserException;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.vo.ActivityInfo;
import com.gzu.queswer.model.vo.UserInfo;
import com.gzu.queswer.service.ActivityService;
import com.gzu.queswer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    ActivityService activityService;
    @Autowired
    UserContext userContext;

    @PostMapping(value = "/login")
    public JSONObject login(@RequestBody JSONObject loginForm) {
        String username = loginForm.getString("username");
        String password = loginForm.getString("password");
        return userService.login(username, password);
    }

    @GetMapping(value = "/logout")
    public boolean logout(String token) {
        return userService.deleteUserByToken(token);
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
    public Integer updateUser(@RequestBody User user) throws UserException {
        user.setUserId(userContext.getUserId(true));
        return userService.updateUser(user);
    }

    @GetMapping(value = "/getUserInfo")
    public UserInfo getUserInfo(Long peopleId) throws UserException {
        return userService.getUserInfo(peopleId,userContext.getUserId(false));
    }

    @GetMapping("/saveFollow")
    public boolean saveFollow(Long peopleId) throws UserException {
        return userService.saveFollow(peopleId, userContext.getUserId(true));
    }

    @GetMapping("/deleteFollow")
    public boolean deleteFollow(Long peopleId) throws UserException {
        return userService.deleteFollow(peopleId, userContext.getUserId(true));
    }

    @GetMapping("/queryUserInfosByFollowerId")
    public List<UserInfo> queryUserInfosByFollowerId(Long peopleId) throws UserException {
        return userService.queryUserInfosByFollowerId(peopleId, userContext.getUserId(false));
    }

    @GetMapping("/queryFollowerInfosByPeopleId")
    public List<UserInfo> queryFollowerInfosByPeopleId(Long peopleId) throws UserException {
        return userService.queryFollowerInfosByPeopleId(peopleId, userContext.getUserId(false));
    }

    @GetMapping("/queryPeopleActivities")
    public List<ActivityInfo> queryPeopleActivities(Long peopleId, int page, int limit) throws UserException {
        return activityService.queryPeopleActivities(peopleId,userContext.getUserId(false), page, limit);
    }

    @GetMapping("/queryFollowActivities")
    public List<ActivityInfo> queryFollowActivities(int page, int limit) throws UserException {
        return activityService.queryFollowActivities(userContext.getUserId(true), page, limit);
    }


}
