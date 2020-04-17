package com.gzu.queswer.dao.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.AnswerDao;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.info.AnswerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
public class AnswerDaoImpl extends RedisDao {
    @Autowired
    private AnswerDao answerDao;

    public Long insertAnswer(Answer answer) {
        answerDao.insertAnswer(answer);
        Long aId = answer.getaId();
        if (aId != null) {
            try (Jedis jedis = getJedis()) {
                String aidKey = aId.toString();
                jedis.set(aidKey, JSON.toJSONString(answer), SET_PARAMS_THIRTY_MINUTES);
                jedis.zadd(PREFIX_QUESTION + answer.getqId().toString() + SUFFIX_ANSWERS, 0.0, aidKey);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return aId;
    }

    public List<Long> selectRidsByAid(Long aid) {
        List<Long> rids = new ArrayList<>();
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(aid, jedis);
            if (aIdKey != null) {
                String aIdRKey = aIdKey + SUFFIX_REVIEWS;
                Set<String> rIdKeys = jedis.zrange(aIdRKey, 0L, -1L);
                for (String rid_key : rIdKeys) {
                    rids.add(Long.parseLong(rid_key));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return rids;
    }

    public boolean deleteAnswer(Long aid, Long uid) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(aid, jedis);
            if (aIdKey != null) {
                Answer answer = getAnswer(aIdKey, jedis);
                if (answer != null && answer.getuId().equals(uid)) {
                    //删除回答 赞同表 反对表
                    jedis.del(aIdKey, aIdKey + SUFFIX_AGREE, aIdKey + SUFFIX_DISAGREE);
                    //从问题表里删除aid
                    res = jedis.zrem(PREFIX_QUESTION + answer.getqId().toString() + SUFFIX_ANSWERS, answer.getaId().toString()) == 1L;
                    answerDao.deleteAnswerByAid(aid);
                    //删除评论列表
                    String aIdRKey = aIdKey + SUFFIX_REVIEWS;
                    jedis.del(aIdRKey);
                    Set<String> rIdKeys = jedis.zrange(aIdRKey, 0, -1);
                    for (String rIdKey : rIdKeys) {
                        rIdKey=PREFIX_REVIEW + rIdKey;
                        //删除评论以及赞
                        jedis.del(rIdKey, rIdKey + SUFFIX_APPROVES);
                    }
                    res = true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    public boolean updateAnswer(Answer answer) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(answer.getaId(), jedis);
            if (aIdKey != null) {
                Answer oldAnswer = getAnswer(aIdKey, jedis);
                if (oldAnswer.getuId().equals(answer.getuId())) {
                    oldAnswer.setAnonymous(answer.getAnonymous());
                    oldAnswer.setAns(answer.getAns());
                    oldAnswer.setGmtModify(answer.getGmtModify());
                    jedis.set(aIdKey, JSON.toJSONString(oldAnswer), SET_PARAMS_THIRTY_MINUTES);
                    answerDao.updateAnswer(oldAnswer);
                    res = true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    public boolean updateAttitude(Attitude attitude) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(attitude.getaId(), jedis);
            if (aIdKey != null) {
                Transaction transaction = jedis.multi();
                String aid1 = aIdKey + SUFFIX_AGREE;
                String aid0 = aIdKey + SUFFIX_DISAGREE;
                String uIdField = attitude.getuId().toString();
                if (Boolean.TRUE.equals(attitude.getAtti())) {
                    transaction.srem(aid0, uIdField);
                    transaction.sadd(aid1, uIdField);
                } else {
                    transaction.srem(aid1, uIdField);
                    transaction.sadd(aid0, uIdField);
                }
                transaction.exec();
                res = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    public boolean deleteAttitude(Long aid, Long uid) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(aid, jedis);
            if (aIdKey != null) {
                String aid1 = aIdKey + SUFFIX_AGREE;
                String aid0 = aIdKey + SUFFIX_DISAGREE;
                String uIdMember = uid.toString();
                jedis.srem(aid1, uIdMember);
                jedis.srem(aid0, uIdMember);
                res = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    public Answer selectAnswerByAid(Long aid) {
        Answer answer = null;
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(aid, jedis);
            if (aIdKey != null) {
                answer = getAnswer(aIdKey, jedis);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return answer;
    }

    private Answer getAnswer(String aIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(aIdKey), Answer.class);
    }

    public AnswerInfo getAnswerInfo(Long aid, Long uid) {
        AnswerInfo answerInfo = null;
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(aid, jedis);
            if (aIdKey != null) {
                answerInfo = new AnswerInfo();
                answerInfo.setAnswer(getAnswer(aIdKey, jedis));
                String aid1 = aIdKey + SUFFIX_AGREE;
                String aid0 = aIdKey + SUFFIX_DISAGREE;
                answerInfo.setAgree(jedis.scard(aid1));
                answerInfo.setAgainst(jedis.scard(aid0));
                answerInfo.setReviewCount(jedis.zcard(aIdKey + SUFFIX_REVIEWS));
                Boolean attituded = null;
                if (uid != null) {
                    String uIdField = uid.toString();
                    if (Boolean.TRUE.equals(jedis.sismember(aid1, uIdField))) attituded = true;
                    else if (Boolean.TRUE.equals(jedis.sismember(aid0, uIdField))) attituded = false;
                }
                answerInfo.setAttituded(attituded);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return answerInfo;
    }

    @Override
    public String getKey(Long aId, Jedis jedis) {
        String aidKey = PREFIX_ANSWER + aId.toString();
        if (jedis.expire(aidKey, ONE_MINUTE) == 0L) {
            Answer answer = answerDao.selectAnswerbyAid(aId);
            jedis.set(aidKey, answer != null ? JSON.toJSONString(answer) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(aidKey) == 0L ? null : aidKey;
    }

}
