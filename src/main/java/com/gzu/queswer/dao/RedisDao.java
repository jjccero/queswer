package com.gzu.queswer.dao;

import com.gzu.queswer.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class RedisDao {

    protected static final SetParams SET_PARAMS_ONE_MINUTE;
    protected static final SetParams SET_PARAMS_THIRTY_MINUTES;
    protected static final int ONE_MINUTE = 60;
    protected static final int THIRTY_MINUTES = 1800;
    protected static final int DATABASE_ANSWER = 0;
    protected static final int DATABASE_USER = 1;
    protected static final int DATABASE_QUESTION = 2;
    protected static final int DATABASE_REVIEW = 3;
    protected static final int T_QUESTION_INDEX = 4;
    protected static final int T_USER_INDEX = 5;
    protected static final String PREFIX_ANSWER = "answer:";
    protected static final String PREFIX_QUESTION = "question:";
    protected static final String PREFIX_REVIEW = "review:";
    protected static final String PREFIX_USER = "user:";
    protected static final String SUFFIX_ATTITUDES = ":attitudes";
    protected static final String SUFFIX_APPROVES = ":approves";
    protected static final String SUFFIX_REVIEWS = ":reviews";
    protected static final String SUFFIX_FOLLOWERS = ":followers";
    protected static final String SUFFIX_ANSWERS = ":answers";

    static {
        SET_PARAMS_ONE_MINUTE = new SetParams();
        SET_PARAMS_ONE_MINUTE.ex(ONE_MINUTE);
        SET_PARAMS_THIRTY_MINUTES = new SetParams();
        SET_PARAMS_THIRTY_MINUTES.ex(THIRTY_MINUTES);
    }


    public Jedis getJedis() {
        return RedisUtil.getJedis();
    }

    public String getKey(Long id, Jedis jedis) {
        return null;
    }

}
