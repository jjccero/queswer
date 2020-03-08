package com.gzu.queswer.dao;

import com.gzu.queswer.model.Attitude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AttitudeDao extends RedisDao {
    @Value("${t_attitude}")
    int t_attitude;

    @Override
    public void setDatabase() {
        database = t_attitude;
        System.out.println(this.getClass());
    }

    @Autowired
    private AnswerDao answerDao;

    public List selectAttitudeByAid(Long aid) {
        Jedis jedis = getJedis();
        String aid_key = aid.toString();
        String aid1 = aid_key + ":1";
        String aid0 = aid_key + ":0";
        if (!jedis.exists(aid_key)) {//缓存未命中
            jedis.set(aid_key, "1");
            List<Attitude> attitudes = answerDao.selectAttitudesByAid(aid);
            for (Attitude attitude : attitudes) {
                String uid_field = attitude.getUid().toString();
                if (attitude.getAttitude()) jedis.sadd(aid1, uid_field);
                else jedis.sadd(aid0, uid_field);
            }
        }
        List attitudes = new ArrayList<>();
        attitudes.add(jedis.scard(aid1));
        attitudes.add(jedis.scard(aid0));
        closeJedis(jedis);
        return attitudes;
    }

    public Integer insertAttitude(Attitude attitude) {
        int res = 0;
        Jedis jedis = getJedis();
        String aid_key = attitude.getAid().toString();
        if (jedis.exists(aid_key)) {
            Transaction transaction = jedis.multi();
            String aid1 = aid_key + ":1";
            String aid0 = aid_key + ":0";
            String uid_field = attitude.getUid().toString();
            if (attitude.getAttitude()) {
                transaction.srem(aid0, uid_field);
                transaction.sadd(aid1, uid_field);
            } else {
                transaction.srem(aid1, uid_field);
                transaction.sadd(aid0, uid_field);
            }
            transaction.exec();
            res = 1;
        }
        closeJedis(jedis);
        return res;
    }

    public Integer deleteAttitude(Long aid, Long uid) {
        int res = 0;
        Jedis jedis = getJedis();
        String aid_key = aid.toString();
        if (jedis.exists(aid_key)) {
            String aid1 = aid_key + ":1";
            String aid0 = aid_key + ":0";
            String uid_field = uid.toString();
            jedis.srem(aid1, uid_field);
            jedis.srem(aid0, uid_field);
            res = 1;
        }
        closeJedis(jedis);
        return res;
    }

    public Boolean selectAttitudeByUid(Long aid, Long uid) {
        Boolean res = null;
        Jedis jedis = getJedis();
        String aid_key = aid.toString();
        if (jedis.exists(aid_key)) {
            String aid1 = aid_key + ":1";
            String aid0 = aid_key + ":0";
            String uid_field = uid.toString();
            if (jedis.sismember(aid1, uid_field)) res = true;
            else if (jedis.sismember(aid0, uid_field)) res = false;
        }
        closeJedis(jedis);
        return res;
    }
}
