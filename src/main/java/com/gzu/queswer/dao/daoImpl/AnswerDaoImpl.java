package com.gzu.queswer.dao.daoImpl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.AnswerDao;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.info.AnswerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class AnswerDaoImpl extends RedisDao {
    @Autowired
    private AnswerDao answerDao;

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

    public List selectRidsByAid(Long aid) {
        List<Long> rids = new ArrayList<>();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String aid_key = getKey(aid, jedis);
            if (aid_key != null) {
                String aid_r_key = getAid_r_key(aid_key);
                Set<String> rid_keys = jedis.zrange(aid_r_key, 0L, -1L);
                for (String rid_key : rid_keys) {
                    rids.add(Long.parseLong(rid_key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return rids;
    }

    public boolean deleteAnswer(Long aid, Long uid) {
        boolean res = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String aid_key = aid.toString();
            Answer answer = getAnswer(aid_key, jedis);
            if (answer != null && answer.getUid().equals(uid)) {
                jedis.del(aid_key + ":1");
                jedis.del(aid_key + ":0");
                jedis.del(aid_key);
                answerDao.deleteAnswer(aid, uid);
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public boolean updateAnswer(Answer answer) {
        boolean res = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String aid_key = getKey(answer.getAid(), jedis);
            if (aid_key != null) {
                Answer old_answer = getAnswer(aid_key, jedis);
                if (old_answer.getUid().equals(answer.getUid())) {
                    old_answer.setAnonymous(answer.getAnonymous());
                    old_answer.setAnswer(answer.getAnswer());
                    old_answer.setModify_answer_time(answer.getModify_answer_time());
                    jedis.set(aid_key, JSON.toJSONString(old_answer), setParams_30m);
                    answerDao.updateAnswer(old_answer);
                    res = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public boolean updateAttitude(Attitude attitude) {
        boolean res = false;
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
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public boolean deleteAttitude(Long aid, Long uid) {
        boolean res = false;
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
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public Answer selectAnswerByAid(Long aid) {
        Jedis jedis = null;
        Answer answer = null;
        try {
            jedis = getJedis();
            String aid_key = getKey(aid, jedis);
            if (aid_key != null) {
                answer = getAnswer(aid_key, jedis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return answer;
    }

    private Answer getAnswer(String aid_key, Jedis jedis) {
        return JSON.parseObject(jedis.get(aid_key), Answer.class);
    }

    public AnswerInfo getAnswerInfo(Long aid, Long uid) {
        AnswerInfo answerInfo = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String aid_key = getKey(aid, jedis);
            if (aid_key != null) {
                answerInfo = new AnswerInfo();
                answerInfo.setAnswer(getAnswer(aid_key, jedis));
                String aid1 = aid_key + ":1";
                String aid0 = aid_key + ":0";
                answerInfo.setAgree(jedis.scard(aid1));
                answerInfo.setAgainst(jedis.scard(aid0));
                answerInfo.setReviewCount(jedis.zcard(getAid_r_key(aid_key)));
                Boolean attituded = null;
                if (uid != null) {
                    String uid_field = uid.toString();
                    if (jedis.sismember(aid1, uid_field)) attituded = true;
                    else if (jedis.sismember(aid0, uid_field)) attituded = false;
                }
                answerInfo.setAttituded(attituded);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return answerInfo;
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
    int database;

    @Override
    protected Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(database);
        return jedis;
    }

    public void addReview(String aid_key, String rid_key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.zadd(getAid_r_key(aid_key), 0.0, rid_key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    private String getAid_r_key(String aid_key){
        return aid_key+":r";
    }
}
