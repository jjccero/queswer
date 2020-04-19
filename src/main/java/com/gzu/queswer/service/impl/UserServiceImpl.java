package com.gzu.queswer.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.UserDao;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.service.UserService;
import com.gzu.queswer.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl extends RedisService implements UserService {
    @Autowired
    UserDao userDao;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User login(String username, String password) {
        User user = null;
        UserLogin userLogin = userDao.selectUserLoginByUsername(username);
        if (userLogin != null && passwordEncoder.matches(password, userLogin.getPassword())) {
            user = userDao.selectUserByUId(userLogin.getuId());
        }
        return user;
    }

    @Override
    public Long saveUser(UserLogin userLogin) {
        userLogin.setGmtCreate(DateUtil.getUnixTime());
        userLogin.setPassword(passwordEncoder.encode(userLogin.getPassword()));
        userDao.insertUser(userLogin);
        return userLogin.getuId();
    }

    @Override
    public Integer updateUser(User user) {
        return null;
    }

    @Override
    public UserInfo getUserInfo(Long uId, Long selfId) {
        UserInfo userInfo = new UserInfo();
        try (Jedis jedis = getJedis()) {
            String uIdKey = getKey(uId, jedis);
            if (uIdKey != null) {
                userInfo.setUser(getUser(uIdKey, jedis));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return userInfo;
    }

    @Override
    public boolean saveFollow(Long uId, Long followerId) {
        return userDao.saveFollow(uId, followerId) == 1;
    }

    @Override
    public boolean deleteFollow(Long uId, Long followerId) {
        return userDao.deleteFollow(uId, followerId) == 1;
    }

    @Override
    public List<UserInfo> queryUserInfosByFollowerId(Long followerId, Long selfId) {
        List<Long> peopleIds = userDao.selectUIdsByFollowerId(followerId);
        List<UserInfo> userInfos = new ArrayList<>(peopleIds.size());
        for (Long peopleId : peopleIds) {
            userInfos.add(getUserInfo(peopleId, selfId));
        }
        return userInfos;
    }

    @Override
    public List<UserInfo> queryFollowerInfosIdsByUId(Long uId, Long selfId) {
        List<Long> peopleIds = userDao.selectFollowerIdsByUId(uId);
        List<UserInfo> userInfos = new ArrayList<>(peopleIds.size());
        for (Long peopleId : peopleIds) {
            userInfos.add(getUserInfo(peopleId, selfId));
        }
        return userInfos;
    }

    private User getUser(String uIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(uIdKey), User.class);
    }

    private String getKey(Long uid, Jedis jedis) {
        String uIdKey = PREFIX_USER + uid.toString();
        if (jedis.expire(uIdKey, ONE_MINUTE) == 0L) {
            User user = userDao.selectUserByUId(uid);
            jedis.set(uIdKey, user != null ? JSON.toJSONString(user) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(uIdKey) == 0L ? null : uIdKey;
    }
}
