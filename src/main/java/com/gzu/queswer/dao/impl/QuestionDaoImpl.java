package com.gzu.queswer.dao.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.QuestionDao;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.info.QuestionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Repository
public class QuestionDaoImpl extends RedisDao {
    @Autowired
    private QuestionDao questionDao;

    private Question getQuestion(String qIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(qIdKey), Question.class);
    }

    public Question selectQuestionByQid(Long qid) {
        Jedis jedis = null;
        Question question = null;
        try {
            jedis = getJedis();
            String qid_key = getKey(qid, jedis);
            if (qid_key != null) {
                question = getQuestion(qid_key, jedis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return question;
    }

    public List<Long> selectAidsByQid(Long qid) {
        List<Long> aids = new ArrayList();
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(qid, jedis);
            if (qIdKey != null) {
                String qid_a_key = qIdKey + SUFFIX_ANSWERS;
                Set<String> aid_keys = jedis.zrange(qid_a_key, 0L, -1L);
                for (String aid_key : aid_keys) {
                    aids.add(Long.parseLong(aid_key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aids;
    }

    public void insertQuestion(Question question) {
        questionDao.insertQuestion(question);
        if (question.getqId() != null) {
            try (Jedis jedis = getJedis()) {
                String qIdKey = question.getqId().toString();
                jedis.zadd(TOP_LIST_KEY, 0.0, qIdKey);
                jedis.set(qIdKey, JSON.toJSONString(question), SET_PARAMS_THIRTY_MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Set getQids(int offset, int count) {
        Set<String> qids = null;
        try (Jedis jedis = getJedis()) {
            qids = jedis.zrevrangeByScore(TOP_LIST_KEY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qids;
    }

    public Integer selectFollowCount(Long qId) {
        return questionDao.selectFollowCount(qId);
    }

    private boolean getFollowed(String qid_f_key, String uid_key, Jedis jedis) {
        return jedis.sismember(qid_f_key, uid_key);
    }

    private long getFollowCount(String qid_f_key, Jedis jedis) {
        return jedis.scard(qid_f_key);
    }

    private long getAnswerCount(String qid_a_key, Jedis jedis) {
        return jedis.zcard(qid_a_key);
    }

    private double getViewCount(Long qId, Jedis jedis) {
        return jedis.zscore(TOP_LIST_KEY, qId.toString());
    }

    public Long getTopAid(Long qId) {
        Long aid = null;
        try (Jedis jedis = getJedis()) {
            Set<String> aid_set = jedis.zrevrangeByScore(PREFIX_QUESTION + qId.toString() + SUFFIX_ANSWERS, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0, 1);
            Iterator<String> iterator = aid_set.iterator();
            if (iterator.hasNext()) {
                aid = Long.parseLong(aid_set.iterator().next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aid;
    }

    public QuestionInfo getQuestionInfo(Long qId, Long uid, boolean view) {
        QuestionInfo questionInfo = null;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(qId, jedis);
            if (qIdKey != null) {
                questionInfo = new QuestionInfo();
                if (view) jedis.zincrby(TOP_LIST_KEY, 1.0, qIdKey);
                Question question = getQuestion(qIdKey, jedis);
                questionInfo.setQuestion(question);
                String qid_f_key = qIdKey + SUFFIX_FOLLOWERS;
                questionInfo.setFollowCount(getFollowCount(qid_f_key, jedis));
                questionInfo.setViewCount(getViewCount(qId, jedis));
                questionInfo.setQuestioned(question.getuId().equals(uid));
                questionInfo.setAnswerCount(getAnswerCount(qIdKey + SUFFIX_ANSWERS, jedis));
                if (uid != null) {
                    questionInfo.setFollowed(getFollowed(qid_f_key, uid.toString(), jedis));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questionInfo;
    }

    public Integer insertFollow(Long qId, Long uid) {
        int res = 0;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(qId, jedis);
            if (qIdKey != null) {
                jedis.sadd(qIdKey + SUFFIX_FOLLOWERS, uid.toString());
                questionDao.insertFollow(qId, uid);
                res = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public Integer deleteFollow(Long qId, Long uid) {
        int res = 0;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(qId, jedis);
            if (qIdKey != null) {
                jedis.srem(qIdKey + SUFFIX_FOLLOWERS, uid.toString());
                questionDao.deleteFollow(qId, uid);
                res = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public List selectFollowsByUid(Long uid) {
        return null;
    }

    final static String TOP_LIST_KEY = "t";

    @Override
    public Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(DATABASE_QUESTION);
        return jedis;
    }

    @Override
    public String getKey(Long qId, Jedis jedis) {
        String qIdKey = PREFIX_QUESTION + qId.toString();
        if (jedis.expire(qIdKey, ONE_MINUTE) == 0L) {
            Question question = questionDao.selectQuestionByQid(qId);
            jedis.set(qIdKey, question != null ? JSON.toJSONString(question) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(qIdKey) == 0L ? null : qIdKey;
    }

    public Long selectAidByUid(Long qId, Long uid) {
        return questionDao.selectAidByUid(qId, uid);
    }
}
