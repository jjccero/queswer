package com.gzu.queswer.dao;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AnswerDaoImpl extends RedisDao {
    @Autowired
    private AnswerDao answerDao;

    public List selectAttitudesByAid(Long aid) {
        List attitudes = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String aid_key=aid.toString();
            attitudes = new ArrayList<>();
            attitudes.add(jedis.scard(aid_key + ":1"));
            attitudes.add(jedis.scard(aid_key + ":0"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return attitudes;
    }

    public void insertAnswer(Answer answer) {
        answerDao.insertAnswer(answer);
        if (answer.getAid() != null) {
            Jedis jedis = null;
            try {
                jedis = getJedis();
                jedis.set(answer.getAid().toString(), JSON.toJSONString(answer), setParams_30m);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jedis != null)
                    jedis.close();
            }
        }
    }

    public Integer deleteAnswer(Long aid, Long uid) {
        int res = 0;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String aid_key = aid.toString();
            Answer answer=getAnswer(aid_key,jedis);
            if (answer!=null&&answer.getUid().equals(uid)) {
                jedis.del(aid_key + ":1");
                jedis.del(aid_key + ":0");
                jedis.del(aid_key);
                answerDao.deleteAnswer(aid, uid);
                res = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public Integer updateAnswer(Answer answer) {
        return null;
    }

    public Integer insertAttitude(Attitude attitude) {
        int res = 0;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String aid_key = getKey(attitude.getAid(), jedis);
            if (aid_key != null) {
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public Integer deleteAttitude(Long aid, Long uid) {
        int res = 0;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String aid_key = getKey(aid, jedis);
            if (aid_key != null) {
                String aid1 = aid_key + ":1";
                String aid0 = aid_key + ":0";
                String uid_field = uid.toString();
                jedis.srem(aid1, uid_field);
                jedis.srem(aid0, uid_field);
                res = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public Boolean selectAttitudeByUid(Long aid, Long uid) {
        Boolean res = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String aid_key = getKey(aid, jedis);
            if (aid_key != null) {
                String aid1 = aid_key + ":1";
                String aid0 = aid_key + ":0";
                String uid_field = uid.toString();
                if (jedis.sismember(aid1, uid_field)) res = true;
                else if (jedis.sismember(aid0, uid_field)) res = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public List selectAnswersByQid(Long qid) {
        List<Answer> answers = new ArrayList<>();
        List<Long> aids = answerDao.selectAidsByQid(qid);
        Jedis jedis = null;
        try {
            jedis = getJedis();
            for (Long aid : aids) {
                String aid_key = getKey(aid, jedis);
                answers.add(getAnswer(aid_key,jedis));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return answers;
    }

    private Answer getAnswer(String aid_key,Jedis jedis){
        return JSON.parseObject(jedis.get(aid_key), Answer.class);
    }


    public Answer selectAnswerByUid(Long qid, Long uid) {
        return answerDao.selectAnswerByUid(qid, uid);
    }

    @Override
    public String getKey(Long aid, Jedis jedis) {
        String aid_key = aid.toString();
        if (jedis.expire(aid_key, second_30m) == 0L) {
            Answer answer = answerDao.selectAnswerbyAid(aid);
            jedis.set(aid_key, answer != null ? JSON.toJSONString(answer) : "", setParams_30m);
        }
        return jedis.strlen(aid_key) == 0L ? null : aid_key;
    }

    @Value("${t_answer}")
    int t_answer;
    @Override
    public Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(t_answer);
        return jedis;
    }
}
