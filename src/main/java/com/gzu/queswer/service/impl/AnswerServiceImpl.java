package com.gzu.queswer.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.AnswerDao;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.info.AnswerInfo;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.service.AnswerService;
import com.gzu.queswer.service.QuestionService;
import com.gzu.queswer.service.UserService;
import com.gzu.queswer.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class AnswerServiceImpl extends RedisService implements AnswerService {
    @Autowired
    AnswerDao answerDao;
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;

    @Override
    public Long saveAnswer(Answer answer) {
        answer.setaId(null);
        answer.setGmtCreate(DateUtil.getUnixTime());
        if (answer.getAnonymous() == null) answer.setAnonymous(false);
        answerDao.insertAnswer(answer);
        Long aId = answer.getaId();
        if (aId != null) {
            try (Jedis jedis = getJedis()) {
                jedis.zadd(PREFIX_QUESTION + answer.getqId().toString() + SUFFIX_ANSWERS, 0.0, aId.toString());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return aId;
    }

    @Override
    public boolean deleteAnswer(Long aId, Long uId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(aId, jedis);
            if (aIdKey != null) {
                Answer answer = getAnswer(aIdKey, jedis);
                if (answer != null && answer.getuId().equals(uId)) {
                    //删除回答 赞同表 反对表
                    jedis.del(aIdKey, aIdKey + SUFFIX_AGREE, aIdKey + SUFFIX_DISAGREE);
                    //从问题表里删除aid
                    res = jedis.zrem(PREFIX_QUESTION + answer.getqId().toString() + SUFFIX_ANSWERS, answer.getaId().toString()) == 1L;
                    answerDao.deleteAnswer(aId);
                    //删除评论列表
                    String aIdRKey = aIdKey + SUFFIX_REVIEWS;
                    jedis.del(aIdRKey);
                    Set<String> rIdKeys = jedis.zrange(aIdRKey, 0, -1);
                    for (String rIdKey : rIdKeys) {
                        rIdKey = PREFIX_REVIEW + rIdKey;
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

    @Override
    public boolean updateAnswer(Answer answer) {
        boolean res = false;
        answer.setGmtModify(DateUtil.getUnixTime());
        if (answer.getAnonymous() == null) answer.setAnonymous(false);
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(answer.getaId(), jedis);
            if (aIdKey != null) {
                Answer oldAnswer = getAnswer(aIdKey, jedis);
                if (oldAnswer.getuId().equals(answer.getuId())) {
                    oldAnswer.setAnonymous(answer.getAnonymous());
                    oldAnswer.setAns(answer.getAns());
                    oldAnswer.setGmtModify(answer.getGmtModify());
                    jedis.set(aIdKey, JSON.toJSONString(oldAnswer), SET_PARAMS_ONE_MINUTE);
                    answerDao.updateAnswer(oldAnswer);
                    res = true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
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

    @Override
    public boolean deleteAttitude(Long aId, Long uId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(aId, jedis);
            if (aIdKey != null) {
                String aid1 = aIdKey + SUFFIX_AGREE;
                String aid0 = aIdKey + SUFFIX_DISAGREE;
                String uIdMember = uId.toString();
                jedis.srem(aid1, uIdMember);
                jedis.srem(aid0, uIdMember);
                res = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
    public List<AnswerInfo> queryAnswers(Long qId, Long uId) {
        List<AnswerInfo> answerInfos = null;
        try (Jedis jedis = getJedis()) {
            String qIdAKey = PREFIX_QUESTION + qId + SUFFIX_ANSWERS;
            Set<String> aIdStrings = jedis.zrange(qIdAKey, 0L, -1L);
            answerInfos = new ArrayList<>(aIdStrings.size());
            for (String aIdString : aIdStrings) {
                AnswerInfo answerInfo = getAnswerInfo(Long.parseLong(aIdString), uId);
                answerInfos.add(answerInfo);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return answerInfos;
    }

    @Override
    public Answer getAnswerByAId(Long aId) {
        Answer answer = null;
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(aId, jedis);
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

    public AnswerInfo getAnswerInfo(Long aId, Long uId) {
        AnswerInfo answerInfo = null;
        try (Jedis jedis = getJedis()) {
            String aIdKey = getKey(aId, jedis);
            if (aIdKey != null) {
                answerInfo = new AnswerInfo();
                answerInfo.setAnswer(getAnswer(aIdKey, jedis));
                String aid1 = aIdKey + SUFFIX_AGREE;
                String aid0 = aIdKey + SUFFIX_DISAGREE;
                answerInfo.setAgree(jedis.scard(aid1));
                answerInfo.setAgainst(jedis.scard(aid0));
                answerInfo.setReviewCount(jedis.zcard(aIdKey + SUFFIX_REVIEWS));
                Boolean attituded = null;
                if (uId != null) {
                    String uIdField = uId.toString();
                    if (Boolean.TRUE.equals(jedis.sismember(aid1, uIdField))) attituded = true;
                    else if (Boolean.TRUE.equals(jedis.sismember(aid0, uIdField))) attituded = false;
                }
                answerInfo.setAttituded(attituded);
                setUserInfo(answerInfo, uId);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return answerInfo;
    }

    private void setUserInfo(AnswerInfo answerInfo, Long uid) {
        if (answerInfo == null) return;
        UserInfo userInfo;
        Answer answer = answerInfo.getAnswer();
        Boolean anonymous = answer.getAnonymous();
        if (Boolean.TRUE.equals(anonymous) && !answer.getuId().equals(uid)) {
            userInfo = UserInfo.defaultUserInfo;
            answer.setuId(null);
        } else {
            userInfo = userService.getUserInfo(answer.getuId(), uid);
            userInfo.setAnonymous(anonymous);
        }
        answerInfo.setUserInfo(userInfo);
    }

    private String getKey(Long aId, Jedis jedis) {
        String aidKey = PREFIX_ANSWER + aId.toString();
        if (jedis.expire(aidKey, ONE_MINUTE) == 0L) {
            Answer answer = answerDao.selectAnswerbyAId(aId);
            jedis.set(aidKey, answer != null ? JSON.toJSONString(answer) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(aidKey) == 0L ? null : aidKey;
    }

}
