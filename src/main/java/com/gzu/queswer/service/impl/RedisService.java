package com.gzu.queswer.service.impl;

import com.gzu.queswer.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class RedisService {

    static final SetParams SET_PARAMS_ONE_MINUTE;
    static final SetParams SET_PARAMS_THIRTY_MINUTES;
    static final int ONE_MINUTE = 60;
    static final int THIRTY_MINUTES = 1800;
    static final int T_QUESTION_INDEX = 4;
    static final int T_USER_INDEX = 5;
    static final String PREFIX_ANSWER = "answer:";
    static final String PREFIX_QUESTION = "question:";
    static final String PREFIX_REVIEW = "review:";
    static final String PREFIX_USER = "user:";
    static final String PREFIX_TOPIC = "topic:";
    static final String SUFFIX_APPROVERS = ":approvers";
    static final String SUFFIX_REVIEWS = ":reviews";
    static final String SUFFIX_SUBSCRIBERS = ":subscribers";
    static final String SUFFIX_ANSWERS = ":answers";
    static final String SUFFIX_F0LLOWERS=":followers";
    static final String SUFFIX_AGREE = ":1";
    static final String SUFFIX_AGAINST = ":0";
    static final String TOP_LIST_KEY = "topQuestions";

    static {
        SET_PARAMS_ONE_MINUTE = new SetParams();
        SET_PARAMS_ONE_MINUTE.ex(ONE_MINUTE);
        SET_PARAMS_THIRTY_MINUTES = new SetParams();
        SET_PARAMS_THIRTY_MINUTES.ex(THIRTY_MINUTES);
    }

    Jedis getJedis() {
        return RedisUtil.getJedis();
    }

}
