package com.gzu.queswer.service;

import com.gzu.queswer.dao.UserInfoDao;
import com.gzu.queswer.dao.UserDao;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserInfo;
import com.gzu.queswer.model.UserInfoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserInfoDao userInfoDao;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User login(String username, String password) {
        User user = userDao.selectUserByUsername(username);
        if (user == null) return null;
        String _password = user.getPassword();
        user.setPassword(null);
        return passwordEncoder.matches(password, _password) ? user : null;
    }

    public Long insertUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.insertUser(user);
        return user.getUid();
    }

    public Integer updateUser(User user) {
        return userDao.updateUser(user);
    }

    public Integer insertSupport(Long uid, Long support_uid) {
        return userDao.insertSupport(uid, support_uid);
    }

    public List selectSupportsByUid(Long uid) {
        return userDao.selectSupportsByUid(uid);
    }

    public List selectSupportersByUid(Long support_uid) {
        return userDao.selectSupportersByUid(support_uid);
    }

    public void setUserInfo(UserInfoApi userInfoApi, Long uid) {
        if (userInfoApi == null) return;
        UserInfo userInfo;
        Boolean anonymous = userInfoApi.getAnonymous();
        if (anonymous && uid != userInfoApi.getUid()) {
            userInfo = new UserInfo();
            userInfo.setNickname("匿名用户");
            userInfo.setUid(null);
            userInfoApi.setUid(null);
        } else {
            userInfo = userInfoDao.getUserInfo(userInfoApi.getUid());
            if (userInfo == null) {
                userInfo = userDao.selectUserInfoByUid(userInfoApi.getUid());
                userInfoDao.setUserInfo(userInfo);
            }
        }
        userInfo.setAnonymous(anonymous);
        userInfoApi.setUserInfo(userInfo);
    }

    public void setUserInfo(List<UserInfoApi> list, Long uid) {
        for (UserInfoApi userInfoApi : list) {
            setUserInfo(userInfoApi, uid);
        }
    }
}
