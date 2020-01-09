package com.gzu.queswer.service;

import com.gzu.queswer.dao.UserDao;
import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserDao userDao;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public Integer signup(String username, String password) {
        try {
            return userDao.signup(username, bCryptPasswordEncoder.encode(password), DateUtil.getDate());
        } catch (Exception e) {
            return 0;//用户已存在
        }
    }

    @Override
    public Integer login(String username, String password) {
        try {
            if (bCryptPasswordEncoder.matches(password, userDao.getPassword(username))) return 1;
            return 0;//密码错误
        } catch (Exception e) {
            return 2;//用户不存在
        }
    }

    @Override
    public Integer sendMessage(String username1, String username2, String message) {
        return null;
    }


}
