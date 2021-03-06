package com.gzu.queswer.service;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.common.UserException;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.vo.PasswordForm;
import com.gzu.queswer.model.vo.UserForm;
import com.gzu.queswer.model.vo.UserInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    JSONObject login(String username, String password) throws UserException;

    Long saveUser(UserLogin userLogin) throws UserException;

    boolean updateUser(UserForm userForm);

    boolean updateAuthority(User user);

    boolean uploadAvater(MultipartFile file, Long userId);

    List<UserInfo> queryAdminInfos(Long userId);

    boolean updatePassword(PasswordForm passwordForm);

    UserInfo getUserInfo(Long peopleId, Long userId);

    boolean saveFollow(Long peopleId, Long userId);

    boolean deleteFollow(Long peopleId, Long userId);

    List<UserInfo> queryUserInfosByFollowerId(Long peopleId, Long userId);

    List<UserInfo> queryFollowerInfosByPeopleId(Long peopleId, Long userId);

    User getUserBySessionId(String sessionId);

    boolean deleteUserBySessionId(String sessionId);

    List<UserInfo> queryUserInfos(List<Long> peopleIds, Long userId);
}
