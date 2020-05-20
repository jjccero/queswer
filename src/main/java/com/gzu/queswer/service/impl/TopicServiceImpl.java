package com.gzu.queswer.service.impl;

import com.gzu.queswer.model.vo.QuestionInfo;
import com.gzu.queswer.model.vo.TopicInfo;
import com.gzu.queswer.service.QuestionService;
import com.gzu.queswer.service.TopicService;
import com.gzu.queswer.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class TopicServiceImpl extends RedisService implements TopicService {
    @Autowired
    QuestionService questionService;

    @Override
    public boolean saveSubscribe(String topic, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String topicKey = getKey(topic);
            double gmtCreate = DateUtil.getUnixTime();
            Transaction transaction = jedis.multi();
            transaction.zadd(topicKey + SUFFIX_SUBSCRIBERS, gmtCreate, userId.toString());
            transaction.zadd(getUserTopicKey(userId), gmtCreate, topic);
            transaction.exec();
            res = true;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    @Override
    public boolean deleteSubscribe(String topic, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String topicKey = getKey(topic);
            Transaction transaction = jedis.multi();
            transaction.zrem(topicKey + SUFFIX_SUBSCRIBERS, userId.toString());
            transaction.zrem(getUserTopicKey(userId), topic);
            transaction.exec();
            res = true;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    @Override
    public TopicInfo getTopicInfo(String topic, Long userId) {
        TopicInfo topicInfo = new TopicInfo();
        try (Jedis jedis = getJedis()) {
            String topicKey = getKey(topic);
            topicInfo.setSubscribeCount(jedis.zcard(topicKey + SUFFIX_SUBSCRIBERS));
            topicInfo.setSubscribed(jedis.zrank(getUserTopicKey(userId), topic) != null);
            topicInfo.setQuestionInfos(queryTopicQuestionInfos(topic, userId));
        } catch (Exception e) {
            log.error(e.toString());
        }
        return topicInfo;
    }

    @Override
    public Set<String> queryTopicsByUserId(Long userId) {
        try (Jedis jedis = getJedis()) {
            return jedis.zrange(getUserTopicKey(userId), 0L, -1L);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new HashSet<>();
    }

    @Override
    public List<QuestionInfo> queryTopicQuestionInfosByUserId(Long userId) {
        try (Jedis jedis = getJedis()) {
            //判断内存中是否存在
            String tempKey = getTempKey(userId, jedis);
            if (tempKey == null) {
                tempKey = PREFIX_USER + userId + SUFFIX_TEMP_TOPICS;
                //若不存在则求用户的话题，转为数组
                Set<String> topics = jedis.zrange(getUserTopicKey(userId), 0L, -1L);
                String[] topicKeys = new String[topics.size()];
                int i = 0;
                for (String topic : topics)
                    topicKeys[i++] = PREFIX_TOPIC + topic;
                Transaction transaction = jedis.multi();
                //通过数组求问题的交集
                transaction.sunionstore(tempKey, topicKeys);
                //存到内存中
                transaction.expire(tempKey, ONE_MINUTE);
                transaction.exec();
            }
            List<String> questionIdStrings = jedis.srandmember(tempKey, 5);
            List<QuestionInfo> questionInfos = new ArrayList<>(questionIdStrings.size());
            for (String questionIdString : questionIdStrings) {
                QuestionInfo questionInfo = questionService.getQuestionInfo(Long.parseLong(questionIdString), userId, false);
                if (questionInfo != null)
                    questionInfos.add(questionInfo);
            }
            return questionInfos;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new ArrayList<>();
    }

    private List<QuestionInfo> queryTopicQuestionInfos(String topic, Long userId) {
        try (Jedis jedis = getJedis()) {
            String topicKey = getKey(topic);
            Set<String> questionIdStrings = jedis.smembers(topicKey);
            List<QuestionInfo> questionInfos = new ArrayList<>(questionIdStrings.size());
            for (String questionIdString : questionIdStrings) {
                QuestionInfo questionInfo = questionService.getQuestionInfo(Long.parseLong(questionIdString), userId, false);
                if (questionInfo != null)
                    questionInfos.add(questionInfo);
            }
            return questionInfos;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new ArrayList<>();
    }

    private String getUserTopicKey(Long userId) {
        return PREFIX_USER + userId + SUFFIX_SUBSCRIBE_TOPIC;
    }

    private String getKey(String topic) {
        return PREFIX_TOPIC + topic;
    }

    private String getTempKey(Long userId, Jedis jedis) {
        String tempKey = PREFIX_USER + userId + SUFFIX_TEMP_TOPICS;
        return jedis.expire(tempKey, ONE_MINUTE) != 0L ? tempKey : null;
    }
}
