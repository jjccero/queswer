package com.gzu.queswer.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.QuestionDao;
import com.gzu.queswer.model.Action;
import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.info.QuestionInfo;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.service.*;
import com.gzu.queswer.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class QuestionServiceImpl extends RedisService implements QuestionService {
    @Autowired
    QuestionDao questionDao;
    @Autowired
    UserService userService;
    @Autowired
    TopicService topicService;
    @Autowired
    AnswerService answerService;
    @Autowired
    ActivityService activityService;

    @Override
    public QuestionInfo getQuestionInfo(Long questionId, Long userId, boolean inc) {
        QuestionInfo questionInfo = null;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(questionId, jedis);
            if (qIdKey != null) {
                questionInfo = new QuestionInfo();
                //增加访问量
                if (inc) jedis.zincrby(TOP_LIST_KEY, 1.0, questionId.toString());
                Question question = getQuestion(qIdKey, jedis);
                //从缓存获取问题主体
                questionInfo.setQuestion(question);
                String qIdSKey = qIdKey + SUFFIX_SUBSCRIBERS;
                //订阅问题数量
                questionInfo.setSubscribeCount(getSubscribeCount(qIdSKey, jedis));
                //观看量
                questionInfo.setViewCount(getViewCount(questionId, jedis));
                //提问者
                questionInfo.setQuestioned(question.getUserId().equals(userId));
                //获取回答数量
                questionInfo.setAnswerCount(getAnswerCount(qIdKey + SUFFIX_ANSWERS, jedis));
                //是否订阅
                if (userId != null) {
                    questionInfo.setSubscribed(getFollowed(qIdSKey, userId, jedis));
                }
                questionInfo.setTopics(topicService.queryTopicsByQuestionId(questionId));
                setUserInfo(questionInfo, userId);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return questionInfo;
    }

    @Override
    public Long saveQuestion(Question question) {
        question.setQuestionId(null);
        question.setGmtCreate(DateUtil.getUnixTime());
        questionDao.insertQuestion(question);
        Long qId = question.getQuestionId();
        if (qId != null) {
            try (Jedis jedis = getJedis()) {
                jedis.zadd(TOP_LIST_KEY, 0.0, qId.toString());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            activityService.saveActivity(getQuestionActivity(question));
        }
        return qId;
    }

    @Override
    public QuestionInfo getQuestionInfo(Long questionId, Long answerId, Long userId, boolean userAnswer, boolean inc) {
        QuestionInfo questionInfo = getQuestionInfo(questionId, userId, inc);
        questionInfo.setTopics(topicService.queryTopicsByQuestionId(questionId));
        Long userAId = null;
        if (userId != null && userAnswer) {
            userAId = selectAidByUid(questionId, userId);
            if (userAId != null) questionInfo.setUserAnswer(answerService.getAnswerInfo(userAId, userId));
        }
        if (answerId != null && !answerId.equals(userAId)) questionInfo.setDefaultAnswer(answerService.getAnswerInfo(answerId, userId));
        return questionInfo;
    }

    @Override
    public List<QuestionInfo> queryQuestions(int offset, int limit, Long userId) {
        List<Long> qIds = queryQIds(offset, limit);
        List<QuestionInfo> questionInfos = new ArrayList<>(qIds.size());
        for (Long qId : qIds) {
            Long aId = getTopAId(qId);
            QuestionInfo questionInfo = getQuestionInfo(qId, aId, userId, false, false);
            questionInfos.add(questionInfo);
        }
        return questionInfos;
    }

    @Override
    public boolean saveSubscribe(Long questionId, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(questionId, jedis);
            if (qIdKey != null) {
                jedis.sadd(qIdKey + SUFFIX_SUBSCRIBERS, userId.toString());
                activityService.saveActivity(getSubscribeActivity(questionId, userId, DateUtil.getUnixTime()));
                res = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
    public boolean deleteSubscribe(Long questionId, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(questionId, jedis);
            if (qIdKey != null) {
                jedis.srem(qIdKey + SUFFIX_SUBSCRIBERS, userId.toString());
                activityService.deleteActivity(getSubscribeActivity(questionId, userId, null));
                res = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
    public Question getQuestionByQId(Long questionId) {
        Question question = null;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(questionId, jedis);
            if (qIdKey != null) {
                question = getQuestion(qIdKey, jedis);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return question;
    }

    private List<Long> queryQIds(int offset, int count) {
        List<Long> qIds = null;
        try (Jedis jedis = getJedis()) {
            Set<String> qIdKeys = jedis.zrevrangeByScore(TOP_LIST_KEY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, offset, count);
            qIds = new ArrayList<>(qIdKeys.size());
            for (String qIdKey : qIdKeys) {
                qIds.add(Long.parseLong(qIdKey));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            qIds = new ArrayList<>();
        }
        return qIds;
    }

    private boolean getFollowed(String qIdFKey, Long uId, Jedis jedis) {
        return jedis.sismember(qIdFKey, uId.toString());
    }

    private long getSubscribeCount(String qIdFKey, Jedis jedis) {
        return jedis.scard(qIdFKey);
    }

    private long getAnswerCount(String qIdAKey, Jedis jedis) {
        return jedis.zcard(qIdAKey);
    }

    private double getViewCount(Long qId, Jedis jedis) {
        return jedis.zscore(TOP_LIST_KEY, qId.toString());
    }

    private Long getTopAId(Long qId) {
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

    private String getKey(Long qId, Jedis jedis) {
        String qIdKey = PREFIX_QUESTION + qId.toString();
        if (jedis.expire(qIdKey, ONE_MINUTE) == 0L) {
            Question question = questionDao.selectQuestion(qId);
            jedis.set(qIdKey, question != null ? JSON.toJSONString(question) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(qIdKey) == 0L ? null : qIdKey;
    }

    private void setUserInfo(QuestionInfo questionInfo, Long uId) {
        if (questionInfo == null) return;
        Question question = questionInfo.getQuestion();
        UserInfo userInfo;
        Boolean anonymous = question.getAnonymous();
        if (Boolean.TRUE.equals(anonymous) && !question.getUserId().equals(uId)) {
            userInfo = UserInfo.defaultUserInfo;
            question.setUserId(null);
        } else {
            userInfo = userService.getUserInfo(question.getUserId(), uId);
            userInfo.setAnonymous(anonymous);
        }
        questionInfo.setUserInfo(userInfo);
    }

    private Question getQuestion(String qIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(qIdKey), Question.class);
    }

    private Long selectAidByUid(Long qId, Long uid) {
        return questionDao.selectAnswerIdByUserId(qId, uid);
    }

    private Activity getSubscribeActivity(Long qId, Long uId, Long gmtCreate) {
        Activity activity = new Activity();
        activity.setUserId(uId);
        activity.setId(qId);
        activity.setAct(Action.SUBSCRIBE_QUESTION);
        activity.setGmtCreate(gmtCreate);
        return activity;
    }

    private Activity getQuestionActivity(Question question) {
        Activity activity = new Activity();
        activity.setUserId(question.getUserId());
        activity.setId(question.getQuestionId());
        activity.setAct(Action.SAVE_QUESTION);
        activity.setGmtCreate(question.getGmtCreate());
        return activity;
    }
}
