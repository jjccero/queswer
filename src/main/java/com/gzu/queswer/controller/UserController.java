package com.gzu.queswer.controller;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.common.UserContext;
import com.gzu.queswer.common.UserException;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.vo.ActivityInfo;
import com.gzu.queswer.model.vo.PasswordForm;
import com.gzu.queswer.model.vo.UserForm;
import com.gzu.queswer.model.vo.UserInfo;
import com.gzu.queswer.service.ActivityService;
import com.gzu.queswer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        return userService.saveUser(userLogin);
    }

    @PostMapping(value = "/updateAuthority")
    public boolean updateAuthority(@RequestBody User user) throws UserException {
        userContext.check(UserLogin.SUPER_ADMIN, true);
        return userService.updateAuthority(user);
    }

    @PostMapping("/updateUser")
    public boolean updateUser(@RequestBody UserForm userForm) throws UserException {
        userForm.setUserId(userContext.getUserId(true));
        return userService.updateUser(userForm);
    }

    @PostMapping("/updatePassword")
    public boolean updatePassword(@RequestBody PasswordForm passwordForm) throws UserException {
        passwordForm.setUserId(userContext.getUserId(true));
        return userService.updatePassword(passwordForm);
    }

    @GetMapping(value = "/getUserInfo")
    public UserInfo getUserInfo(Long peopleId) throws UserException {
        return userService.getUserInfo(peopleId, userContext.getUserId(false));
    }

    @GetMapping("/saveFollow")
    public boolean saveFollow(Long peopleId) throws UserException {
        return userService.saveFollow(peopleId, userContext.getUserId(true));
    }

    @GetMapping("/deleteFollow")
    public boolean deleteFollow(Long peopleId) throws UserException {
        return userService.deleteFollow(peopleId, userContext.getUserId(true));
    }

    //    @PostMapping("/queryUserInfos")
//    public List<UserInfo> queryUserInfos(@RequestBody String peopleIdsString) throws UserException {
//        JSONArray peopleIds = JSON.parseArray(peopleIdsString);
//        return userService.queryUserInfos(peopleIds.toJavaList(Long.class), userContext.getUserId(false));
//    }
    @PostMapping("/queryUserInfos")
    public List<UserInfo> queryUserInfos(@RequestBody List<Long> peopleIds) throws UserException {
        return userService.queryUserInfos(peopleIds, userContext.getUserId(false));
    }

    @GetMapping("/queryAdminInfos")
    public List<UserInfo> queryAdminInfos() throws UserException {
        userContext.check(UserLogin.SUPER_ADMIN, true);
        return userService.queryAdminInfos(userContext.getUserId(false));
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
        return activityService.queryPeopleActivities(peopleId, userContext.getUserId(false), page, limit);
    }

    @GetMapping("/queryFollowActivities")
    public List<ActivityInfo> queryFollowActivities(int page, int limit) throws UserException {
        return activityService.queryFollowActivities(userContext.getUserId(true), page, limit);
    }

    @PostMapping("/uploadAvater")
    public boolean uploadAvater(@RequestBody MultipartFile file) throws UserException {
        return userService.uploadAvater(file, userContext.getUserId(true));
    }
}
