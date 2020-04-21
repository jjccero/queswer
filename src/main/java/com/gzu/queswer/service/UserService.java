package com.gzu.queswer.service;

import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.info.UserInfo;

import java.util.List;

public interface UserService {
    User login(String username, String password);

    Long saveUser(UserLogin userLogin);

    Integer updateUser(User user);

    UserInfo getUserInfo(Long peopleId, Long userId);

    boolean saveFollow(Long peopleId,Long userId);

    boolean deleteFollow(Long peopleId,Long userId);

    List<UserInfo> queryUserInfosByFollowerId(Long peopleId,Long userId);

    List<UserInfo> queryFollowerInfosIdsByUId(Long peopleId,Long userId);

}
