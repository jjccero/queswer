package com.gzu.queswer.dao.daoImpl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.AnswerDao;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.info.AnswerInfo;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Long insertAnswer(Answer answer) {
        answerDao.insertAnswer(answer);
        Long aid = answer.getAid();
        if (aid != null) {
            Jedis jedis = null;
            try {
                jedis = getJedis();
                String aid_key = aid.toString();
                jedis.set(aid_key, JSON.toJSONString(answer), setParams_30m);
                jedis.select(t_question);
                jedis.zadd(answer.getQid().toString() + ":a", 0.0, aid_key);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jedis != null)
                    jedis.close();
            }
        }
        return aid;
    }

    public List selectRidsByAid(Long aid) {
        List<Long> rids = new ArrayList<>();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String aid_key = getKey(aid, jedis);
            if (aid_key != null) {
                String aid_r_key = aid_key + ":r";
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
                //删除回答 赞同表 反对表
                jedis.del(aid_key, aid_key + ":1", aid_key + ":0");
                jedis.select(t_question);
                //从问题表里删除aid
                res = jedis.zrem(answer.getQid().toString() + ":a", aid_key) == 1L;
                answerDao.deleteAnswerByAid(aid);
                String aid_r_key = aid_key + ":r";
                Set<String> rid_keys = jedis.zrange(aid_r_key, 0, -1);
                jedis.del(aid_r_key);
                jedis.select(t_review);
                for (String rid_key : rid_keys) {
                    //删除评论以及赞
                    jedis.del(rid_key, rid_key + ":a");
                }
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
                    old_answer.setGmt_modify(answer.getGmt_modify());
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
                String uid_member = uid.toString();
                jedis.srem(aid1, uid_member);
                jedis.srem(aid0, uid_member);
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
                answerInfo.setReviewCount(jedis.zcard(aid_key + ":r"));
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

    @Override
    public Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(t_answer);
        return jedis;
    }

}
