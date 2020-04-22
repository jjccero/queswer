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

    /**
     * people所关注的人
     * @param peopleId
     * @param userId
     * @return
     */
    List<UserInfo> queryUserInfosByFollowerId(Long peopleId, Long userId);

    /**
     * 关注people的人
     * @param peopleId
     * @param userId
     * @return
     */
    List<UserInfo> queryFollowerInfosByPeopleId(Long peopleId, Long userId);

}
