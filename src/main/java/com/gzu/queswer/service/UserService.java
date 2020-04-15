package com.gzu.queswer.service;

import com.gzu.queswer.dao.UserDao;
import com.gzu.queswer.dao.impl.UserDaoImpl;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserDaoImpl userDaoImpl;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User login(String username, String password) {
        User user = null;
        UserLogin userLogin = userDao.selectUserLoginByUsername(username);
        if (userLogin != null && passwordEncoder.matches(password, userLogin.getPassword())) {
            user = userDao.selectUserByUid(userLogin.getuId());
        }
        return user;
    }

    public Long insertUser(UserLogin userLogin) {
        userLogin.setGmtCreate(DateUtil.getUnixTime());
        userLogin.setPassword(passwordEncoder.encode(userLogin.getPassword()));
        userDao.insertUser(userLogin);
        return userLogin.getuId();
    }

    public Integer updateUser(User user) {
        return userDao.updateUser(user);
    }

    public UserInfo selectUserInfo(Long uid, Long user_uid) {
        return userDaoImpl.selectUserInfo(uid, user_uid);
    }

}
