package com.gzu.queswer.service;

import com.gzu.queswer.dao.UserDao;
import com.gzu.queswer.dao.UserInfoDao;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserInfo;
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

    public UserInfo getUserInfo(Long uid) {
        return userInfoDao.getUserInfo(uid);
    }

//    public void setUserInfo(JSONObject jsonObject, Long uid) {
//        if (jsonObject == null) return;
//        UserInfo userInfo;
//        Boolean anonymous = jsonObject.getBoolean("anonymous");
//        Long uid2=jsonObject.getLong("uid");
//        if (anonymous && !uid.equals(uid2)) {
//            userInfo = new UserInfo();
//            userInfo.setNickname("匿名用户");
//            userInfo.setUid(null);
//            userInfo.setIntro(null);
//            jsonObject.put("uid",null);
//        } else {
//            userInfo = userInfoDao.getUserInfo(uid2);
//            if (userInfo == null) {
//                userInfo = userDao.selectUserInfoByUid(uid2);
//                userInfoDao.setUserInfo(userInfo);
//            }
//        }
//        userInfo.setAnonymous(anonymous);
//        jsonObject.put("userInfo",userInfo);
//    }
//
//    public void setUserInfo(JSONArray jsonArray, Long uid) {
//        for(Object jsonObject:jsonArray){
//            setUserInfo((JSONObject) jsonObject,uid);
//        }
//    }
}
