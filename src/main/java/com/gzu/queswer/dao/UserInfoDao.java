package com.gzu.queswer.dao;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

@Repository
public class UserInfoDao extends RedisDao {

    public void setUserInfo(UserInfo userInfo) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(userInfo.getUid().toString(), JSONObject.toJSONString(userInfo), setParams_60s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public UserInfo getUserInfo(Long uid) {
        Jedis jedis = null;
        UserInfo userInfo=null;
        try {
            jedis = getJedis();
            String key = uid.toString();
            userInfo = JSONObject.parseObject(jedis.get(key), UserInfo.class);
            if (userInfo != null) jedis.expire(key, second_60s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return userInfo;
    }

    @Value("${t_userInfo}")
    int t_userInfo;

    @Override
    public Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(t_userInfo);
        return jedis;
    }

}
