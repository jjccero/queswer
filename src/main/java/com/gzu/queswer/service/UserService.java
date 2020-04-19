package com.gzu.queswer.service;

import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.info.UserInfo;

import java.util.List;

public interface UserService {
    User login(String username, String password);

    Long saveUser(UserLogin userLogin);

    Integer updateUser(User user);

    UserInfo getUserInfo(Long peopleId, Long uId);

    boolean saveFollow(Long uId,Long followerId);

    boolean deleteFollow(Long uId,Long followerId);

    List<UserInfo> queryUserInfosByFollowerId(Long followerId,Long selfId);

    List<UserInfo> queryFollowerInfosIdsByUId(Long uId,Long selfId);

}
