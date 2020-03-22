package com.gzu.queswer.dao;

import com.gzu.queswer.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class RedisDao {

    final protected static SetParams setParams_60s;
    final protected static SetParams setParams_30m;
    final protected static int second_60s = 60;
    final protected static int second_30m = 1800;
    final protected static int t_answer=0;
    final protected static int t_userInfo=1;
    final protected static int t_question=2;
    final protected static int t_review=3;
    final protected static int t_question_index=4;
    final protected static int t_user_index=5;
    static {
        setParams_60s = new SetParams();
        setParams_60s.ex(second_60s);
        setParams_30m = new SetParams();
        setParams_30m.ex(second_30m);
    }
    void template(){
        Jedis jedis = null;
        try {
            jedis = getJedis();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }
    public Jedis getJedis(){
        return RedisUtil.getJedis();
    }

    public String getKey(Long id, Jedis jedis){
        return null;
    }
}
