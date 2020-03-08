package com.gzu.queswer.dao;

import com.gzu.queswer.util.RedisUtil;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;

public abstract class RedisDao {
    public int database;

    @PostConstruct
    public abstract void setDatabase();

    public Jedis getJedis() {
        return RedisUtil.getJedis(database);
    }

    public void closeJedis(Jedis jedis) {
        RedisUtil.closeJedis(jedis);
    }

}
