package com.gzu.queswer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.dao.UserDao;
import com.gzu.queswer.model.Action;
import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.vo.PasswordForm;
import com.gzu.queswer.model.vo.UserForm;
import com.gzu.queswer.model.vo.UserInfo;
import com.gzu.queswer.service.ActivityService;
import com.gzu.queswer.service.UserService;
import com.gzu.queswer.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl extends RedisService implements UserService {
    @Autowired
    UserDao userDao;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    ActivityService activityService;

    @Override
    public JSONObject login(String username, String password) {
        try (Jedis jedis = getJedis()) {
            UserLogin userLogin = userDao.selectUserLoginByUsername(username);
            if (userLogin != null && passwordEncoder.matches(password, userLogin.getPassword())) {
                User user = userDao.selectUser(userLogin.getUserId());
                if (user != null) {
                    String token = UUID.randomUUID().toString();
                    jedis.select(T_TOKEN);
                    jedis.set(token, user.getUserId().toString(), SET_PARAMS_THIRTY_DAYS);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("token", token);
                    jsonObject.put("user", user);
                    return jsonObject;
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

    @Override
    public Long saveUser(UserLogin userLogin) {
        userLogin.setNormalUser();
        userLogin.setGmtCreate(DateUtil.getUnixTime());
        userLogin.setPassword(passwordEncoder.encode(userLogin.getPassword()));
        userDao.insertUser(userLogin);
        return userLogin.getUserId();
    }

    @Override
    public boolean updateUser(UserForm userForm) {
        if (userDao.updateUser(userForm) == 1) {
            try (Jedis jedis = getJedis()) {
                getKey(userForm.getUserId(), jedis);
            } catch (Exception e) {
                log.error(e.toString());
            }
            return true;
        } else return false;
    }

    @Override
    public boolean updatePassword(PasswordForm passwordForm) {
        passwordForm.setNewPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        String password = userDao.selectPassword(passwordForm.getUserId());
        return passwordEncoder.matches(passwordForm.getOldPassword(), password) && userDao.updatePassword(passwordForm) == 1;
    }

    @Override
    public UserInfo getUserInfo(Long peopleId, Long userId) {
        UserInfo userInfo = new UserInfo();
        try (Jedis jedis = getJedis()) {
            String userIdKey = getKey(peopleId, jedis);
            if (userIdKey != null) {
                userInfo.setUser(getUser(userIdKey, jedis));
                if (userId != null)
                    userInfo.setFollowed(jedis.sismember(userIdKey + SUFFIX_F0LLOWERS, userId.toString()));
                userInfo.setFollowersCount(jedis.scard(userIdKey + SUFFIX_F0LLOWERS));
                userInfo.setFollowCount(jedis.scard(userIdKey + SUFFIX_F0LLOWS));
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return userInfo;
    }

    @Override
    public boolean saveFollow(Long peopleId, Long userId) {
        if (peopleId.equals(userId) || userId == null) return false;
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String userIdKey = getKey(userId, jedis);
            String peopleIdKey = getKey(peopleId, jedis);
            if (userIdKey != null) {
                Transaction transaction = jedis.multi();
                transaction.sadd(peopleIdKey + SUFFIX_F0LLOWERS, userId.toString());
                transaction.sadd(userIdKey + SUFFIX_F0LLOWS, peopleId.toString());
                transaction.exec();
                activityService.saveActivity(getFollowActivity(peopleId, userId, DateUtil.getUnixTime()));
                res = true;
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    @Override
    public boolean deleteFollow(Long peopleId, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String userIdKey = getKey(userId, jedis);
            String peopleIdKey = getKey(peopleId, jedis);
            if (peopleIdKey != null && userIdKey != null) {
                Transaction transaction = jedis.multi();
                transaction.srem(peopleIdKey + SUFFIX_F0LLOWERS, userId.toString());
                transaction.srem(userIdKey + SUFFIX_F0LLOWS, peopleId.toString());
                transaction.exec();
                activityService.deleteActivity(getFollowActivity(peopleId, userId, null));
                res = true;
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    @Override
    public List<UserInfo> queryUserInfosByFollowerId(Long followerId, Long userId) {
        try (Jedis jedis = getJedis()) {
            String followerIdKey = getKey(followerId, jedis);
            if (followerIdKey != null) {
                return getUserInfos(followerIdKey + SUFFIX_F0LLOWS, userId, jedis);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new ArrayList<>();
    }

    @Override
    public List<UserInfo> queryFollowerInfosByPeopleId(Long peopleId, Long userId) {
        try (Jedis jedis = getJedis()) {
            String peopleIdKey = getKey(peopleId, jedis);
            if (peopleIdKey != null) {
                return getUserInfos(peopleIdKey + SUFFIX_F0LLOWERS, userId, jedis);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new ArrayList<>();
    }

    @Override
    public User getUserByToken(String token) {
        if (token == null) return null;
        try (Jedis jedis = getJedis()) {
            jedis.select(T_TOKEN);
            String userIdString = jedis.get(token);
            if (userIdString != null) {
                jedis.select(0);
                String userIdKey = getKey(Long.parseLong(userIdString), jedis);
                if (userIdKey != null)
                    return getUser(userIdKey, jedis);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

    @Override
    public boolean deleteUserByToken(String token) {
        try (Jedis jedis = getJedis()) {
            jedis.select(T_TOKEN);
            return jedis.del(token) == 1L;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return false;
    }

    private List<UserInfo> getUserInfos(String setKey, Long userId, Jedis jedis) {
        Set<String> idStrings = jedis.smembers(setKey);
        List<UserInfo> userInfos = new ArrayList<>(idStrings.size());
        for (String idString : idStrings) {
            userInfos.add(getUserInfo(Long.parseLong(idString), userId));
        }
        return userInfos;
    }

    private User getUser(String userIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(userIdKey), User.class);
    }

    private String getKey(Long userId, Jedis jedis) {
        String userIdKey = PREFIX_USER + userId;
        if (jedis.expire(userIdKey, ONE_MINUTE) == 0L) {
            User user = userDao.selectUser(userId);
            jedis.set(userIdKey, user != null ? JSON.toJSONString(user) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(userIdKey) == 0L ? null : userIdKey;
    }

    private Activity getFollowActivity(Long peopleId, Long userId, Long gmtCreate) {
        Activity activity = new Activity();
        activity.setUserId(userId);
        activity.setAct(Action.FOLLOW_USER);
        activity.setId(peopleId);
        activity.setGmtCreate(gmtCreate);
        return activity;
    }
}
