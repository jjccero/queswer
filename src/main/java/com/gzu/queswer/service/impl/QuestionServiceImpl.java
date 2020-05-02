package com.gzu.queswer.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.QuestionDao;
import com.gzu.queswer.model.Action;
import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.vo.QuestionInfo;
import com.gzu.queswer.model.vo.UserInfo;
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
            String questionIdKey = getKey(questionId, jedis);
            if (questionIdKey != null) {
                questionInfo = new QuestionInfo();
                //增加访问量
                if (inc) jedis.zincrby(TOP_LIST_KEY, 1.0, questionId.toString());
                Question question = getQuestion(questionIdKey, jedis);
                //从缓存获取问题主体
                questionInfo.setQuestion(question);
                String questionIdSKey = questionIdKey + SUFFIX_SUBSCRIBERS;
                //订阅问题数量
                questionInfo.setSubscribeCount(getSubscribeCount(questionIdSKey, jedis));
                //观看量
                questionInfo.setViewCount(getViewCount(questionId, jedis));
                //提问者
                questionInfo.setQuestioned(question.getUserId().equals(userId));
                //获取回答数量
                questionInfo.setAnswerCount(getAnswerCount(questionIdKey + SUFFIX_ANSWERS, jedis));
                //是否订阅
                if (userId != null) {
                    questionInfo.setSubscribed(getFollowed(questionIdSKey, userId, jedis));
                }
                questionInfo.setTopics(topicService.queryTopicsByQuestionId(questionId));
                setUserInfo(questionInfo, userId);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return questionInfo;
    }

    @Override
    public Long saveQuestion(Question question) {
        question.setQuestionId(null);
        question.setGmtCreate(DateUtil.getUnixTime());
        questionDao.insertQuestion(question);
        Long questionId = question.getQuestionId();
        if (questionId != null) {
            try (Jedis jedis = getJedis()) {
                jedis.zadd(TOP_LIST_KEY, 0.0, questionId.toString());
            } catch (Exception e) {
                log.error(e.toString());
            }
            activityService.saveActivity(getQuestionActivity(question));
        }
        return questionId;
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
        if (answerId != null && !answerId.equals(userAId))
            questionInfo.setDefaultAnswer(answerService.getAnswerInfo(answerId, userId));
        return questionInfo;
    }

    @Override
    public List<QuestionInfo> queryQuestions(int page, int limit, Long userId) {
        List<Long> questionIds = queryQuestionIds(page, limit);
        List<QuestionInfo> questionInfos = new ArrayList<>(questionIds.size());
        for (Long questionId : questionIds) {
            Long aId = getTopAnswerId(questionId);
            QuestionInfo questionInfo = getQuestionInfo(questionId, aId, userId, false, false);
            questionInfos.add(questionInfo);
        }
        return questionInfos;
    }

    @Override
    public boolean saveSubscribe(Long questionId, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String questionIdKey = getKey(questionId, jedis);
            if (questionIdKey != null) {
                jedis.sadd(questionIdKey + SUFFIX_SUBSCRIBERS, userId.toString());
                activityService.saveActivity(getSubscribeActivity(questionId, userId, DateUtil.getUnixTime()));
                res = true;
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    @Override
    public boolean deleteSubscribe(Long questionId, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String questionIdKey = getKey(questionId, jedis);
            if (questionIdKey != null) {
                jedis.srem(questionIdKey + SUFFIX_SUBSCRIBERS, userId.toString());
                activityService.deleteActivity(getSubscribeActivity(questionId, userId, null));
                res = true;
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    @Override
    public Question getQuestion(Long questionId) {
        Question question = null;
        try (Jedis jedis = getJedis()) {
            String questionIdKey = getKey(questionId, jedis);
            if (questionIdKey != null) {
                question = getQuestion(questionIdKey, jedis);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return question;
    }

    @Override
    public List<QuestionInfo> queryQuestionsByUserId(Long peopleId, Long userId) {
        List<QuestionInfo> questionInfos;
        List<Long> questionIds = questionDao.selectQuestionIdsByUserId(peopleId);
        questionInfos = new ArrayList<>(questionIds.size());
        for (Long questionId : questionIds) {
            QuestionInfo questionInfo = getQuestionInfo(questionId, userId, false);
            if (questionInfo != null)
                questionInfos.add(questionInfo);
        }
        return questionInfos;
    }

    private List<Long> queryQuestionIds(int page, int limit) {
        int offset = page * limit;
        List<Long> questionIds;
        try (Jedis jedis = getJedis()) {
            Set<String> questionIdKeys = jedis.zrevrangeByScore(TOP_LIST_KEY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, offset, limit);
            questionIds = new ArrayList<>(questionIdKeys.size());
            for (String questionIdKey : questionIdKeys) {
                questionIds.add(Long.parseLong(questionIdKey));
            }
        } catch (Exception e) {
            log.error(e.toString());
            questionIds = new ArrayList<>();
        }
        return questionIds;
    }

    private boolean getFollowed(String questionIdFKey, Long uId, Jedis jedis) {
        return jedis.sismember(questionIdFKey, uId.toString());
    }

    private long getSubscribeCount(String questionIdFKey, Jedis jedis) {
        return jedis.scard(questionIdFKey);
    }

    private long getAnswerCount(String questionIdAKey, Jedis jedis) {
        return jedis.zcard(questionIdAKey);
    }

    private double getViewCount(Long questionId, Jedis jedis) {
        return jedis.zscore(TOP_LIST_KEY, questionId.toString());
    }

    private Long getTopAnswerId(Long questionId) {
        Long aid = null;
        try (Jedis jedis = getJedis()) {
            Set<String> aIdKeys = jedis.zrevrangeByScore(PREFIX_QUESTION + questionId.toString() + SUFFIX_ANSWERS, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0, 1);
            Iterator<String> iterator = aIdKeys.iterator();
            if (iterator.hasNext()) {
                aid = Long.parseLong(aIdKeys.iterator().next());
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return aid;
    }

    private String getKey(Long questionId, Jedis jedis) {
        String questionIdKey = PREFIX_QUESTION + questionId.toString();
        if (jedis.expire(questionIdKey, ONE_MINUTE) == 0L) {
            Question question = questionDao.selectQuestion(questionId);
            jedis.set(questionIdKey, question != null ? JSON.toJSONString(question) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(questionIdKey) == 0L ? null : questionIdKey;
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

    private Question getQuestion(String questionIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(questionIdKey), Question.class);
    }

    private Long selectAidByUid(Long questionId, Long uid) {
        return questionDao.selectAnswerIdByUserId(questionId, uid);
    }

    private Activity getSubscribeActivity(Long questionId, Long uId, Long gmtCreate) {
        Activity activity = new Activity();
        activity.setUserId(uId);
        activity.setId(questionId);
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
