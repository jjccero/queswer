package com.gzu.queswer.dao.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.QuestionDao;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.info.QuestionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
public class QuestionDaoImpl extends RedisDao {
    @Autowired
    private QuestionDao questionDao;

    private Question getQuestion(String qIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(qIdKey), Question.class);
    }

    public Question selectQuestionByQid(Long qid) {
        Question question = null;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(qid, jedis);
            if (qIdKey != null) {
                question = getQuestion(qIdKey, jedis);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return question;
    }

    public List<Long> selectAIds(Long qId) {
        List<Long> aIds = null;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(qId, jedis);
            if (qIdKey != null) {
                String qIdAKey = qIdKey + SUFFIX_ANSWERS;
                Set<String> aIdKeys = jedis.zrange(qIdAKey, 0L, -1L);
                aIds = new ArrayList<>(aIdKeys.size());
                for (String aIdKey : aIdKeys) {
                    aIds.add(Long.parseLong(aIdKey));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return aIds;
    }

    public void insertQuestion(Question question) {
        questionDao.insertQuestion(question);
        if (question.getqId() != null) {
            try (Jedis jedis = getJedis()) {
                String qIdKey = question.getqId().toString();
                jedis.zadd(TOP_LIST_KEY, 0.0, qIdKey);
                jedis.set(qIdKey, JSON.toJSONString(question), SET_PARAMS_THIRTY_MINUTES);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public List<Long> queryQIds(int offset, int count) {
        List<Long> qIds = new ArrayList<>(count);
        try (Jedis jedis = getJedis()) {
            Set<String> qIdKeys = jedis.zrevrangeByScore(TOP_LIST_KEY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, offset, count);
            for (String qIdKey : qIdKeys) {
                qIds.add(Long.parseLong(qIdKey));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return qIds;
    }

    private boolean getFollowed(String qIdFKey, Long uId, Jedis jedis) {
        return jedis.sismember(qIdFKey, uId.toString());
    }

    private long getFollowCount(String qIdFKey, Jedis jedis) {
        return jedis.scard(qIdFKey);
    }

    private long getAnswerCount(String qIdAKey, Jedis jedis) {
        return jedis.zcard(qIdAKey);
    }

    private double getViewCount(Long qId, Jedis jedis) {
        return jedis.zscore(TOP_LIST_KEY, qId.toString());
    }

    public Long getTopAid(Long qId) {
        Long aid = null;
        try (Jedis jedis = getJedis()) {
            Set<String> aIdKeys = jedis.zrevrangeByScore(PREFIX_QUESTION + qId.toString() + SUFFIX_ANSWERS, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0, 1);
            Iterator<String> iterator = aIdKeys.iterator();
            if (iterator.hasNext()) {
                aid = Long.parseLong(aIdKeys.iterator().next());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return aid;
    }

    public QuestionInfo getQuestionInfo(Long qId, Long uId, boolean view) {
        QuestionInfo questionInfo = null;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(qId, jedis);
            if (qIdKey != null) {
                questionInfo = new QuestionInfo();
                if (view) jedis.zincrby(TOP_LIST_KEY, 1.0, qIdKey);
                Question question = getQuestion(qIdKey, jedis);
                questionInfo.setQuestion(question);
                String qIdFKey = qIdKey + SUFFIX_FOLLOWERS;
                questionInfo.setFollowCount(getFollowCount(qIdFKey, jedis));
                questionInfo.setViewCount(getViewCount(qId, jedis));
                questionInfo.setQuestioned(question.getuId().equals(uId));
                questionInfo.setAnswerCount(getAnswerCount(qIdKey + SUFFIX_ANSWERS, jedis));
                if (uId != null) {
                    questionInfo.setFollowed(getFollowed(qIdFKey, uId, jedis));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
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
            log.error(e.getMessage());
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
            log.error(e.getMessage());
        }
        return res;
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
