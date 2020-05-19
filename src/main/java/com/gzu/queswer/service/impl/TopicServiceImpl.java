package com.gzu.queswer.service.impl;

import com.gzu.queswer.model.vo.TopicInfo;
import com.gzu.queswer.service.TopicService;
import com.gzu.queswer.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

@Slf4j
@Service
public class TopicServiceImpl extends RedisService implements TopicService {
    @Override
    public boolean saveSubscribe(String topic, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String topicKey = getKey(topic);
            double gmtCreate = DateUtil.getUnixTime();
            Transaction transaction = jedis.multi();
            transaction.zadd(topicKey + SUFFIX_SUBSCRIBERS,gmtCreate, userId.toString());
            transaction.zadd(PREFIX_USER + userId + SUFFIX_SUBSCRIBE_TOPIC,gmtCreate, topic);
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
            transaction.zrem(PREFIX_USER + userId + SUFFIX_SUBSCRIBE_TOPIC, topic);
            transaction.exec();
            res = true;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    @Override
    public TopicInfo getTopicInfo(String topic, Long userId) {
        try (Jedis jedis = getJedis()) {
            String topicKey = getKey(topic);
            TopicInfo topicInfo = new TopicInfo();
            topicInfo.setSubscribeCount(jedis.zcard(topicKey + SUFFIX_SUBSCRIBERS));
            topicInfo.setSubscribed(jedis.zrank(PREFIX_USER + userId + SUFFIX_SUBSCRIBE_TOPIC, topic) != null);
            return topicInfo;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

    private String getKey(String topic) {
        return PREFIX_TOPIC + topic;
    }
}
