package com.gzu.queswer.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisUtil {
    private static JedisPool jedisPool;
    private static int database = 0;

    static {
//        JedisPoolConfig config = new JedisPoolConfig();
        jedisPool = new JedisPool();
    }

    public static Jedis getJedis() {
        if (jedisPool != null) return jedisPool.getResource();
        return null;
    }

    public static void closeJedis(final Jedis jedis) {
        jedis.close();
    }

    public static int getDatabase() {
        return database;
    }

    public static void setDatabase(int database) {
        RedisUtil.database = database;
    }
}
