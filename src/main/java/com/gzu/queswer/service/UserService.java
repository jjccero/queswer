package com.gzu.queswer.service;

import com.gzu.queswer.dao.UserDao;
import com.gzu.queswer.dao.daoImpl.UserDaoImpl;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.util.DateUtil;
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
    private UserDaoImpl userDaoImpl;

    final static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public User login(String username, String password) {
        User user=null;
        UserLogin userLogin=userDao.selectUserLoginByUsername(username);
        if (userLogin != null&&passwordEncoder.matches(password,userLogin.getPassword())) {
            user= userDao.selectUserByUid(userLogin.getUid());
        }
        return user;
    }

    public Long insertUser(UserLogin userLogin) {
        userLogin.setGmt_create(DateUtil.getUnixTime());
        userLogin.setPassword(passwordEncoder.encode(userLogin.getPassword()));
        userDao.insertUser(userLogin);
        return userLogin.getUid();
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
        return userDaoImpl.getUserInfo(uid);
    }

}
