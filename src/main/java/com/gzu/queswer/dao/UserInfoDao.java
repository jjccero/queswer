package com.gzu.queswer.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

@Repository
public class UserInfoDao extends RedisDao {

    public UserInfo getUserInfo(Long uid) {
        Jedis jedis = null;
        UserInfo userInfo = null;
        try {
            jedis = getJedis();
            String uid_key = getKey(uid, jedis);
            if (uid_key != null) {
                userInfo = JSONObject.parseObject(jedis.get(uid_key), UserInfo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return userInfo;
    }

    @Autowired
    UserDao userdao;

    @Override
    public String getKey(Long uid, Jedis jedis) {
        String uid_key = uid.toString();
        if (jedis.expire(uid_key, second_60s) == 0L) {
            UserInfo userInfo = userdao.selectUserInfoByUid(uid);
            jedis.set(uid_key, userInfo != null ? JSON.toJSONString(userInfo) : "", setParams_30m);
        }
        return jedis.strlen(uid_key) == 0L ? null : uid_key;
    }

    @Value("${t_userInfo}")
    int database;

    @Override
    public Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(database);
        return jedis;
    }

}
