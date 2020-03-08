package com.gzu.queswer.dao;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Repository
public class UserInfoDao extends RedisDao {

    private static final int SECONDS = 60;
    private static SetParams setParams;

    static {
        setParams = new SetParams();
        setParams.ex(SECONDS);
    }

    public void setUserInfo(UserInfo userInfo) {
        Jedis jedis = getJedis();
        jedis.set(userInfo.getUid().toString(), JSONObject.toJSONString(userInfo), setParams);
        closeJedis(jedis);
    }

    public UserInfo getUserInfo(Long uid) {
        Jedis jedis = getJedis();
        String key = uid.toString();
        UserInfo userInfo = JSONObject.parseObject(jedis.get(key), UserInfo.class);
        if (userInfo != null) jedis.expire(key, SECONDS);
        closeJedis(jedis);
        return userInfo;
    }

    @Value("${t_userInfo}")
    int t_userInfo;

    @Override
    public void setDatabase() {
        database = t_userInfo;
        System.out.println(this.getClass());
    }
}
