package com.gzu.queswer.dao;

import com.gzu.queswer.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class RedisDao {

    protected static final SetParams setParams_60s;
    protected static final SetParams setParams_30m;
    protected static final int second_60s = 60;
    protected static final int second_30m = 1800;
    protected static final int t_answer = 0;
    protected static final int t_userInfo = 1;
    protected static final int t_question = 2;
    protected static final int t_review = 3;
    protected static final int t_question_index = 4;
    protected static final int t_user_index = 5;

    static {
        setParams_60s = new SetParams();
        setParams_60s.ex(second_60s);
        setParams_30m = new SetParams();
        setParams_30m.ex(second_30m);
    }

    void template() {
        try (Jedis jedis = getJedis()) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Jedis getJedis() {
        return RedisUtil.getJedis();
    }

    public String getKey(Long id, Jedis jedis) {
        return null;
    }
}
