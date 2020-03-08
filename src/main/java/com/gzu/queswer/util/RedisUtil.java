package com.gzu.queswer.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisUtil {
    private static JedisPool jedisPool;

    static {
        jedisPool = new JedisPool();
    }

    public static Jedis getJedis(int database) {
        Jedis jedis= jedisPool.getResource();
        jedis.select(database);
        return jedis;
    }

    public static void closeJedis(final Jedis jedis) {
        jedis.close();
    }

}
