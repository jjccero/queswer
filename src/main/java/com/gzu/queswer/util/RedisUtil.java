package com.gzu.queswer.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisUtil {
    private RedisUtil() {
    }

    private static JedisPool jedisPool;

    static {
        jedisPool = new JedisPool();
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

}
