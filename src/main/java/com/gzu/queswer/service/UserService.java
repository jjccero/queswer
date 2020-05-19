package com.gzu.queswer.service;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.vo.PasswordForm;
import com.gzu.queswer.model.vo.UserForm;
import com.gzu.queswer.model.vo.UserInfo;

import java.util.List;

public interface UserService {
    JSONObject login(String username, String password);

    Long saveUser(UserLogin userLogin);

    boolean updateUser(UserForm userForm);

    boolean updatePassword(PasswordForm passwordForm);

    UserInfo getUserInfo(Long peopleId, Long userId);

    boolean saveFollow(Long peopleId, Long userId);

    boolean deleteFollow(Long peopleId, Long userId);

    List<UserInfo> queryUserInfosByFollowerId(Long peopleId, Long userId);
    
    List<UserInfo> queryFollowerInfosByPeopleId(Long peopleId, Long userId);

    User getUserByToken(String token);

    boolean deleteUserByToken(String token);
}
