package com.gzu.queswer.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.UserDao;
import com.gzu.queswer.model.Action;
import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.service.ActivityService;
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
    @Autowired
    ActivityService activityService;

    @Override
    public User login(String username, String password) {
        User user = null;
        UserLogin userLogin = userDao.selectUserLoginByUsername(username);
        if (userLogin != null && passwordEncoder.matches(password, userLogin.getPassword())) {
            user = userDao.selectUser(userLogin.getUserId());
        }
        return user;
    }

    @Override
    public Long saveUser(UserLogin userLogin) {
        userLogin.setGmtCreate(DateUtil.getUnixTime());
        userLogin.setPassword(passwordEncoder.encode(userLogin.getPassword()));
        userDao.insertUser(userLogin);
        return userLogin.getUserId();
    }

    @Override
    public Integer updateUser(User user) {
        return null;
    }

    @Override
    public UserInfo getUserInfo(Long peopleId, Long userId) {
        UserInfo userInfo = new UserInfo();
        try (Jedis jedis = getJedis()) {
            String userIdKey = getKey(peopleId, jedis);
            if (userIdKey != null) {
                userInfo.setUser(getUser(userIdKey, jedis));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return userInfo;
    }

    @Override
    public boolean saveFollow(Long peopleId, Long userId) {
        Long gmtCreate = DateUtil.getUnixTime();
        return userDao.saveFollow(peopleId, userId, gmtCreate) == 1
                && activityService.saveActivity(getFollowActivity(peopleId, userId, gmtCreate));
    }

    @Override
    public boolean deleteFollow(Long peopleId, Long userId) {
        return userDao.deleteFollow(peopleId, userId) == 1
                && activityService.deleteActivity(getFollowActivity(peopleId, userId, null));
    }

    @Override
    public List<UserInfo> queryUserInfosByFollowerId(Long followerId, Long userId) {
        List<Long> peopleIds = userDao.selectUserIdsByFollowerId(followerId);
        List<UserInfo> userInfos = new ArrayList<>(peopleIds.size());
        for (Long peopleId : peopleIds) {
            userInfos.add(getUserInfo(peopleId, userId));
        }
        return userInfos;
    }

    @Override
    public List<UserInfo> queryFollowerInfosIdsByUId(Long userId, Long selfId) {
        List<Long> peopleIds = userDao.selectFollowerIdsByUserId(userId);
        List<UserInfo> userInfos = new ArrayList<>(peopleIds.size());
        for (Long peopleId : peopleIds) {
            userInfos.add(getUserInfo(peopleId, selfId));
        }
        return userInfos;
    }

    private User getUser(String userIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(userIdKey), User.class);
    }

    private String getKey(Long userId, Jedis jedis) {
        String userIdKey = PREFIX_USER + userId.toString();
        if (jedis.expire(userIdKey, ONE_MINUTE) == 0L) {
            User user = userDao.selectUser(userId);
            jedis.set(userIdKey, user != null ? JSON.toJSONString(user) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(userIdKey) == 0L ? null : userIdKey;
    }

    private Activity getFollowActivity(Long uId, Long followerId, Long gmtCreate) {
        Activity activity = new Activity();
        activity.setUserId(followerId);
        activity.setAct(Action.FOLLOW_USER);
        activity.setId(uId);
        activity.setGmtCreate(gmtCreate);
        return activity;
    }
}
