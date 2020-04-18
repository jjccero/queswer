package com.gzu.queswer.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.QuestionDao;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.info.QuestionInfo;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.service.AnswerService;
import com.gzu.queswer.service.QuestionService;
import com.gzu.queswer.service.TopicService;
import com.gzu.queswer.service.UserService;
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

    @Override
    public QuestionInfo getQuestionInfo(Long qId, Long uId, boolean inc) {
        QuestionInfo questionInfo = null;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(qId, jedis);
            if (qIdKey != null) {
                questionInfo = new QuestionInfo();
                //增加访问量
                if (inc) jedis.zincrby(TOP_LIST_KEY, 1.0, qId.toString());
                Question question = getQuestion(qIdKey, jedis);
                //从缓存获取问题主体
                questionInfo.setQuestion(question);
                String qIdFKey = qIdKey + SUFFIX_SUBSCRIBERS;
                //订阅问题数量
                questionInfo.setSubscribeCount(getSubscribeCount(qIdFKey, jedis));
                //观看量
                questionInfo.setViewCount(getViewCount(qId, jedis));
                //提问者
                questionInfo.setQuestioned(question.getuId().equals(uId));
                //获取回答数量
                questionInfo.setAnswerCount(getAnswerCount(qIdKey + SUFFIX_ANSWERS, jedis));
                //是否订阅
                if (uId != null) {
                    questionInfo.setSubscribed(getFollowed(qIdFKey, uId, jedis));
                }
                questionInfo.setTopics(topicService.selectQuestionTopics(qId));
                setUserInfo(questionInfo, uId);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return questionInfo;
    }

    @Override
    public Long saveQuestion(Question question) {
        question.setqId(null);
        question.setGmtCreate(DateUtil.getUnixTime());
        questionDao.insertQuestion(question);
        Long qId = question.getqId();
        if (qId != null) {
            try (Jedis jedis = getJedis()) {
                jedis.zadd(TOP_LIST_KEY, 0.0, qId.toString());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return qId;
    }

    @Override
    public QuestionInfo getQuestionInfo(Long qId, Long aId, Long uId, boolean userAnswer, boolean inc) {
        QuestionInfo questionInfo = getQuestionInfo(qId, uId, inc);
        questionInfo.setTopics(topicService.selectQuestionTopics(qId));
        Long userAId = null;
        if (uId != null && userAnswer) {
            userAId = selectAidByUid(qId, uId);
            if (userAId != null) questionInfo.setUserAnswer(answerService.getAnswerInfo(userAId, uId));
        }
        if (aId != null && !aId.equals(userAId)) questionInfo.setDefaultAnswer(answerService.getAnswerInfo(aId, uId));
        return questionInfo;
    }

    @Override
    public List<QuestionInfo> queryQuestions(int offset, int count, Long uId) {
        List<Long> qIds = queryQIds(offset, count);
        List<QuestionInfo> questionInfos = new ArrayList<>();
        for (Long qId : qIds) {
            Long aId = getTopAId(qId);
            QuestionInfo questionInfo = getQuestionInfo(qId, aId, uId, false, false);
            questionInfos.add(questionInfo);
        }
        return questionInfos;
    }

    @Override
    public boolean saveSubscribe(Long qId, Long uId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(qId, jedis);
            if (qIdKey != null) {
                jedis.sadd(qIdKey + SUFFIX_SUBSCRIBERS, uId.toString());
                res = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
    public boolean deleteSubscribe(Long qId, Long uId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(qId, jedis);
            if (qIdKey != null) {
                jedis.srem(qIdKey + SUFFIX_SUBSCRIBERS, uId.toString());
                res = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
    public Question getQuestionByQid(Long qId) {
        Question question = null;
        try (Jedis jedis = getJedis()) {
            String qIdKey = getKey(qId, jedis);
            if (qIdKey != null) {
                question = getQuestion(qIdKey, jedis);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return question;
    }

    @Override
    public List<Long> queryAIdsByQId(Long qId) {
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
            Question question = questionDao.selectQuestionByQid(qId);
            jedis.set(qIdKey, question != null ? JSON.toJSONString(question) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(qIdKey) == 0L ? null : qIdKey;
    }

    private void setUserInfo(QuestionInfo questionInfo, Long uId) {
        if (questionInfo == null) return;
        Question question = questionInfo.getQuestion();
        UserInfo userInfo;
        Boolean anonymous = question.getAnonymous();
        if (Boolean.TRUE.equals(anonymous) && !question.getuId().equals(uId)) {
            userInfo = UserInfo.defaultUserInfo;
            question.setuId(null);
        } else {
            userInfo = userService.getUserInfo(question.getuId(), uId);
            userInfo.setAnonymous(anonymous);
        }
        questionInfo.setUserInfo(userInfo);
    }

    private Question getQuestion(String qIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(qIdKey), Question.class);
    }

    private Long selectAidByUid(Long qId, Long uid) {
        return questionDao.selectAidByUid(qId, uid);
    }
}
