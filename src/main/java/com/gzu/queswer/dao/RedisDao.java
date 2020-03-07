package com.gzu.queswer.dao;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.UserInfo;
import com.gzu.queswer.util.RedisUtil;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Repository
public class RedisDao {
    private static final int SECONDS=60;
    private static SetParams setParams;
    static {
        setParams=new SetParams();
        setParams.ex(SECONDS);
    }
    public void setUserInfo(UserInfo userInfo){
        Jedis jedis= RedisUtil.getJedis();
        jedis.set(userInfo.getUid().toString(), JSONObject.toJSONString(userInfo),setParams);
        RedisUtil.closeJedis(jedis);
    }
    public UserInfo getUserInfo(Long uid){
        Jedis jedis= RedisUtil.getJedis();
        String key=uid.toString();
        UserInfo userInfo=JSONObject.parseObject(jedis.get(key),UserInfo.class);
        if(userInfo!=null) jedis.expire(key,SECONDS);
        RedisUtil.closeJedis(jedis);
        return userInfo;
    }
}
