package com.gzu.queswer.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.dao.UserDao;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.info.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

@Repository
public class UserDaoImpl extends RedisDao {

    public UserInfo selectUserInfo(Long uid,Long user_uid) {
        UserInfo userInfo = new UserInfo();
        try (Jedis jedis = getJedis()) {
            String uid_key = getKey(uid, jedis);
            if (uid_key != null) {
                userInfo.setUser(getUser(uid_key, jedis));
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return userInfo;
    }

    public User getUser(String uid_key,Jedis jedis){
        return JSONObject.parseObject(jedis.get(uid_key), User.class);
    }
    @Autowired
    UserDao userdao;

    @Override
    public String getKey(Long uid, Jedis jedis) {
        String uid_key = uid.toString();
        if (jedis.expire(uid_key, second_30m) == 0L) {
            User user = userdao.selectUserByUid(uid);
            jedis.set(uid_key, user != null ? JSON.toJSONString(user) : "", setParams_30m);
        }
        return jedis.strlen(uid_key) == 0L ? null : uid_key;
    }

    @Override
    public Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(t_userInfo);
        return jedis;
    }

}
