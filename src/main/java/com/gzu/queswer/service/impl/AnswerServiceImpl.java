package com.gzu.queswer.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.AnswerDao;
import com.gzu.queswer.model.*;
import com.gzu.queswer.model.info.AnswerInfo;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.service.ActivityService;
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
    @Autowired
    ActivityService activityService;

    @Override
    public Long saveAnswer(Answer answer) {
        answer.setAnswerId(null);
        answer.setGmtCreate(DateUtil.getUnixTime());
        if (answer.getAnonymous() == null) answer.setAnonymous(false);
        answerDao.insertAnswer(answer);
        Long answerId = answer.getAnswerId();
        if (answerId != null) {
            try (Jedis jedis = getJedis()) {
                jedis.zadd(PREFIX_QUESTION + answer.getQuestionId().toString() + SUFFIX_ANSWERS, 0.0, answerId.toString());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            activityService.saveActivity(getAnswerActivity(answer));
        }
        return answerId;
    }

    @Override
    public boolean deleteAnswer(Long answerId, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String answerIdKey = getKey(answerId, jedis);
            if (answerIdKey != null) {
                Answer answer = getAnswer(answerIdKey, jedis);
                if (answer != null && answer.getUserId().equals(userId)) {
                    //删除回答 赞同表 反对表
                    jedis.del(answerIdKey, answerIdKey + SUFFIX_AGREE, answerIdKey + SUFFIX_AGAINST);
                    //从问题表里删除aid
                    res = jedis.zrem(PREFIX_QUESTION + answer.getQuestionId().toString() + SUFFIX_ANSWERS, answer.getAnswerId().toString()) == 1L;
                    answerDao.deleteAnswer(answerId);
                    //删除评论列表
                    String answerIdRKey = answerIdKey + SUFFIX_REVIEWS;
                    jedis.del(answerIdRKey);
                    Set<String> rIdKeys = jedis.zrange(answerIdRKey, 0, -1);
                    for (String rIdKey : rIdKeys) {
                        rIdKey = PREFIX_REVIEW + rIdKey;
                        //删除评论以及赞
                        jedis.del(rIdKey, rIdKey + SUFFIX_APPROVERS);
                    }
                    activityService.deleteActivity(getAnswerActivity(answer));
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
            String answerIdKey = getKey(answer.getAnswerId(), jedis);
            if (answerIdKey != null) {
                Answer oldAnswer = getAnswer(answerIdKey, jedis);
                if (oldAnswer.getUserId().equals(answer.getUserId())) {
                    oldAnswer.setAnonymous(answer.getAnonymous());
                    oldAnswer.setAns(answer.getAns());
                    oldAnswer.setGmtModify(answer.getGmtModify());
                    jedis.set(answerIdKey, JSON.toJSONString(oldAnswer), SET_PARAMS_ONE_MINUTE);
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
        attitude.setGmtCreate(DateUtil.getUnixTime());
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String answerIdKey = getKey(attitude.getAnswerId(), jedis);
            if (answerIdKey != null) {
                Transaction transaction = jedis.multi();
                String aid1 = answerIdKey + SUFFIX_AGREE;
                String aid0 = answerIdKey + SUFFIX_AGAINST;
                String userIdField = attitude.getUserId().toString();
                if (Boolean.TRUE.equals(attitude.getAtti())) {
                    transaction.srem(aid0, userIdField);
                    transaction.sadd(aid1, userIdField);
                } else {
                    transaction.srem(aid1, userIdField);
                    transaction.sadd(aid0, userIdField);
                }
                transaction.exec();
                activityService.saveActivity(getAttitudeActivity(attitude.getAnswerId(), attitude.getUserId(), attitude.getGmtCreate()));
                res = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
    public boolean deleteAttitude(Long answerId, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String answerIdKey = getKey(answerId, jedis);
            if (answerIdKey != null) {
                String aid1 = answerIdKey + SUFFIX_AGREE;
                String aid0 = answerIdKey + SUFFIX_AGAINST;
                String userIdMember = userId.toString();
                jedis.srem(aid1, userIdMember);
                jedis.srem(aid0, userIdMember);
                activityService.deleteActivity(getAttitudeActivity(answerId, userId, null));
                res = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
    public List<AnswerInfo> queryAnswers(Long questionId, Long userId) {
        List<AnswerInfo> answerInfos = null;
        try (Jedis jedis = getJedis()) {
            String qIdAKey = PREFIX_QUESTION + questionId + SUFFIX_ANSWERS;
            Set<String> answerIdStrings = jedis.zrange(qIdAKey, 0L, -1L);
            answerInfos = new ArrayList<>(answerIdStrings.size());
            for (String answerIdString : answerIdStrings) {
                AnswerInfo answerInfo = getAnswerInfo(Long.parseLong(answerIdString), userId);
                answerInfos.add(answerInfo);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return answerInfos;
    }

    @Override
    public Answer getAnswer(Long answerId) {
        Answer answer = null;
        try (Jedis jedis = getJedis()) {
            String answerIdKey = getKey(answerId, jedis);
            if (answerIdKey != null) {
                answer = getAnswer(answerIdKey, jedis);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return answer;
    }

    @Override
    public boolean getAgree(Long answerId, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String answerIdKey = getKey(answerId, jedis);
            res = Boolean.TRUE.equals(jedis.sismember(answerIdKey + SUFFIX_AGREE, userId.toString()));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    private Answer getAnswer(String answerIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(answerIdKey), Answer.class);
    }

    public AnswerInfo getAnswerInfo(Long answerId, Long userId) {
        AnswerInfo answerInfo = null;
        try (Jedis jedis = getJedis()) {
            String answerIdKey = getKey(answerId, jedis);
            if (answerIdKey != null) {
                answerInfo = new AnswerInfo();
                answerInfo.setAnswer(getAnswer(answerIdKey, jedis));
                String aid1 = answerIdKey + SUFFIX_AGREE;
                String aid0 = answerIdKey + SUFFIX_AGAINST;
                answerInfo.setAgree(jedis.scard(aid1));
                answerInfo.setAgainst(jedis.scard(aid0));
                answerInfo.setReviewCount(jedis.zcard(answerIdKey + SUFFIX_REVIEWS));
                Boolean attituded = null;
                if (userId != null) {
                    if (Boolean.TRUE.equals(jedis.sismember(aid1, userId.toString()))) attituded = true;
                    else if (Boolean.TRUE.equals(jedis.sismember(aid0, userId.toString()))) attituded = false;
                }
                answerInfo.setAttituded(attituded);
                setUserInfo(answerInfo, userId);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return answerInfo;
    }

    private void setUserInfo(AnswerInfo answerInfo, Long userId) {
        if (answerInfo == null) return;
        UserInfo userInfo;
        Answer answer = answerInfo.getAnswer();
        Boolean anonymous = answer.getAnonymous();
        if (Boolean.TRUE.equals(anonymous) && !answer.getUserId().equals(userId)) {
            userInfo = UserInfo.defaultUserInfo;
            answer.setUserId(null);
        } else {
            userInfo = userService.getUserInfo(answer.getUserId(), userId);
            userInfo.setAnonymous(anonymous);
        }
        answerInfo.setUserInfo(userInfo);
    }

    private String getKey(Long answerId, Jedis jedis) {
        String answerIdKey = PREFIX_ANSWER + answerId.toString();
        if (jedis.expire(answerIdKey, ONE_MINUTE) == 0L) {
            Answer answer = answerDao.selectAnswer(answerId);
            jedis.set(answerIdKey, answer != null ? JSON.toJSONString(answer) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(answerIdKey) == 0L ? null : answerIdKey;
    }

    private Activity getAttitudeActivity(Long answerId, Long userId, Long gmtCreate) {
        Activity activity = new Activity();
        activity.setUserId(userId);
        activity.setId(answerId);
        activity.setAct(Action.ATTITUDE_ANSWER);
        activity.setGmtCreate(gmtCreate);
        return activity;
    }

    private Activity getAnswerActivity(Answer answer) {
        Activity activity = new Activity();
        activity.setUserId(answer.getUserId());
        activity.setId(answer.getAnswerId());
        activity.setAct(Action.SAVE_ANSWER);
        activity.setGmtCreate(answer.getGmtCreate());
        return activity;
    }
}
