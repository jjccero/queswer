package com.gzu.queswer.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class QuestionDaoImpl extends RedisDao {
    @Autowired
    private QuestionDao questionDao;

    @Deprecated
    public Question selectQuestionByQid(Long qid) {
        Jedis jedis = null;
        Question question = null;
        try {
            jedis = getJedis();
            String qid_key = getKey(qid, jedis);
            if (qid_key != null) {
                question = JSON.parseObject(jedis.get(qid_key), Question.class);
                jedis.zincrby(TOP_LIST_KEY, 1.0, qid_key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return question;
    }

    public void insertQuestion(Question question) {
        questionDao.insertQuestion(question);
        if (question.getQid() != null) {
            Jedis jedis = null;
            try {
                jedis = getJedis();
                String qid_key = question.getQid().toString();
                jedis.zadd(TOP_LIST_KEY, 0.0, qid_key);
                jedis.set(qid_key, JSON.toJSONString(question), setParams_30m);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jedis != null)
                    jedis.close();
            }
        }
    }

    public List selectQuestions(int offset, int limit) {
        List<Question> questions = new ArrayList<>();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Set<String> qids = jedis.zrange(TOP_LIST_KEY, offset, offset + limit);
            for (String qid_key : qids) {
                getKey(Long.parseLong(qid_key), jedis);
                questions.add(JSON.parseObject(jedis.get(qid_key), Question.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return questions;
    }

    public Integer selectFollowCount(Long qid) {
        return questionDao.selectFollowCount(qid);
    }

    public JSONObject getQuestionInfo(Long qid, Long uid) {
        JSONObject jsonObject = new JSONObject();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String qid_key = getKey(qid, jedis);
            if (qid_key != null) {
                jedis.zincrby(TOP_LIST_KEY, 1.0, qid_key);
                Question question = JSON.parseObject(jedis.get(qid_key), Question.class);
                jsonObject.put("question", question);
                String qid_f_key = qid_key + ":f";
                jsonObject.put("viewCount", jedis.zscore(TOP_LIST_KEY, qid_key));
                jsonObject.put("followCount", jedis.scard(qid_f_key));
                jsonObject.put("followed", uid != null ? jedis.sismember(qid_f_key, uid.toString()) : false);
                jsonObject.put("questioned", question.getUid().equals(uid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return jsonObject;
    }

    public Integer insertFollow(Long qid, Long uid) {
        int res = 0;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String qid_key = getKey(qid, jedis);
            if (qid_key != null) {
                jedis.sadd(qid_key + ":f", uid.toString());
                questionDao.insertFollow(qid, uid);
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

    public Integer deleteFollow(Long qid, Long uid) {
        int res = 0;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String qid_key = getKey(qid, jedis);
            if (qid_key != null) {
                jedis.srem(qid_key + ":f", uid.toString());
                questionDao.deleteFollow(qid, uid);
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

    public List selectFollowsByUid(Long uid) {
        return null;
    }

    @Value("${t_question}")
    int t_question;
    final static String TOP_LIST_KEY = "t";

    //    final static String VIEW_LIST_KEY = "v";
    @Override
    protected Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(t_question);
        return jedis;
    }

    @Override
    public String getKey(Long qid, Jedis jedis) {
        String qid_key = qid.toString();
        if (jedis.expire(qid_key, second_30m) == 0L) {
            Question question = questionDao.selectQuestionByQid(qid);
            jedis.set(qid_key, question != null ? JSON.toJSONString(question) : "", setParams_30m);
        }
        return jedis.strlen(qid_key) == 0L ? null : qid_key;
    }
}
