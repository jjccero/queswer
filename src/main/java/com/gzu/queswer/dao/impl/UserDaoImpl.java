package com.gzu.queswer.dao.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.dao.UserDao;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.info.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

@Repository
@Slf4j
public class UserDaoImpl extends RedisDao {

    public UserInfo selectUserInfo(Long uId, Long userUId) {
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

    public User getUser(String uIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(uIdKey), User.class);
    }

    @Autowired
    UserDao userdao;

    @Override
    public String getKey(Long uid, Jedis jedis) {
        String uIdKey = PREFIX_USER + uid.toString();
        if (jedis.expire(uIdKey, ONE_MINUTE) == 0L) {
            User user = userdao.selectUserByUid(uid);
            jedis.set(uIdKey, user != null ? JSON.toJSONString(user) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(uIdKey) == 0L ? null : uIdKey;
    }

    @Override
    public Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(DATABASE_USER);
        return jedis;
    }

}
