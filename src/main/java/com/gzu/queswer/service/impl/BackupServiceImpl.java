package com.gzu.queswer.service.impl;

import com.gzu.queswer.dao.BackupDao;
import com.gzu.queswer.model.Action;
import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.TopicActivity;
import com.gzu.queswer.service.ActivityService;
import com.gzu.queswer.service.BackupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BackupServiceImpl extends RedisService implements BackupService {
    @Autowired
    BackupDao backupDao;
    @Autowired
    ActivityService activityService;

    @Override
    public boolean backup() {
        try (Jedis jedis = getJedis()) {
            List<Long> questionIds = backupDao.selectQuestionIds();
            for (Long questionId : questionIds) {
                jedis.zadd(TOP_LIST_KEY, 0.0, questionId.toString());
                List<Long> answerIds = backupDao.selectAnswerIdsByQuestionId(questionId);
                String questionKey = PREFIX_QUESTION + questionId + SUFFIX_ANSWERS;
                for (Long answerId : answerIds) {
                    jedis.zadd(questionKey, 0.0, answerId.toString());
                    List<Long> reviewIds = backupDao.selectReviewIdsByAnswerId(answerId);
                    String answerKey = PREFIX_ANSWER + answerId + SUFFIX_REVIEWS;
                    for (Long reviewId : reviewIds) {
                        jedis.zadd(answerKey, 0.0, reviewId.toString());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return false;
    }

    @Override
    public boolean restore() {
        try (Jedis jedis = getJedis()) {
            long startTime = System.currentTimeMillis();
            jedis.flushAll();
            List<Long> qIds = backupDao.selectQuestionIds();
            for (Long qId : qIds) {
                jedis.zadd(TOP_LIST_KEY, 0.0, qId.toString());
                List<Long> aIds = backupDao.selectAnswerIdsByQuestionId(qId);
                String qIdKey = PREFIX_QUESTION + qId + SUFFIX_ANSWERS;
                for (Long aId : aIds) {
                    jedis.zadd(qIdKey, 0.0, aId.toString());
                    List<Long> rIds = backupDao.selectReviewIdsByAnswerId(aId);
                    String aIdKey = PREFIX_ANSWER + aId + SUFFIX_REVIEWS;
                    for (Long rId : rIds) {
                        jedis.zadd(aIdKey, 0.0, rId.toString());
                    }
                }
            }
            logTime(startTime, "flushAll");
            //恢复态度表，并加入时间轴
            List<Activity> activities = restoreAttitudeActivities(startTime, jedis);
            logTime(startTime, "restoreAttitudeActivities");
            //提问加入时间轴
            restoreQuestionActivities(activities);
            logTime(startTime, "restoreQuestionActivities");
            //回答加入时间轴
            restoreAnswerActivities(activities);
            logTime(startTime, "restoreAnswerActivities");
            //恢复问题订阅表，并加入时间轴
            restoreSubscribeQuestionActivities(jedis, activities);
            logTime(startTime, "restoreSubscribeQuestionActivities");
            //恢复话题订阅情况加入时间轴
            restoreSubscribeTopicActivities(jedis);
            logTime(startTime, "restoreSubscribeTopicActivities");
            //恢复评论赞同表,不需要加入时间轴
            restoreApproveActivities(jedis);
            logTime(startTime, "restoreApproveActivities");
            for (Activity activity : activities) {
                activityService.saveActivity(activity, jedis);
            }
            logTime(startTime, "insertActivityBatch");
            return true;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return false;
    }

    private void logTime(long startTime, String msg) {
        log.info("{}:{}ms", msg, System.currentTimeMillis() - startTime);
    }

    private void restoreAnswerActivities(List<Activity> activities) {
        List<Activity> answerActivities = backupDao.selectAnswerActivities();
        for (Activity activity : answerActivities) activity.setAct(Action.SAVE_ANSWER);
        activities.addAll(answerActivities);
    }

    private void restoreQuestionActivities(List<Activity> activities) {
        List<Activity> questionActivities = backupDao.selectQuestionActivities();
        for (Activity activity : questionActivities) activity.setAct(Action.SAVE_QUESTION);
        activities.addAll(questionActivities);
    }

    private List<Activity> restoreAttitudeActivities(long startTime, Jedis jedis) {
        List<Attitude> attitudes = backupDao.selectAttitudes();
        List<Activity> activities = new ArrayList<>(attitudes.size());
        logTime(startTime, "activities");
        Transaction transaction = jedis.multi();
        for (Attitude attitude : attitudes) {
            double gmtCreate = attitude.getGmtCreate().doubleValue();
            String userIdString = attitude.getUserId().toString();
            if (Boolean.TRUE.equals(attitude.getAtti()))
                transaction.zadd(PREFIX_ANSWER + attitude.getAnswerId() + SUFFIX_AGREE, gmtCreate, userIdString);
            else
                transaction.zadd(PREFIX_ANSWER + attitude.getAnswerId() + SUFFIX_AGAINST, gmtCreate, userIdString);
            Activity activity = new Activity();
            activity.setUserId(attitude.getUserId());
            activity.setId(attitude.getAnswerId());
            activity.setAct(Action.ATTITUDE_ANSWER);
            activity.setGmtCreate(attitude.getGmtCreate());
            activities.add(activity);
        }
        transaction.exec();
        return activities;
    }

    private void restoreSubscribeQuestionActivities(Jedis jedis, List<Activity> activities) {
        List<Activity> subscribeQuestionActivities = backupDao.selectSubscribeQuestion();
        Transaction transaction = jedis.multi();
        for (Activity activity : subscribeQuestionActivities) {
            jedis.zadd(PREFIX_QUESTION + activity.getId() + SUFFIX_SUBSCRIBERS, activity.getGmtCreate(), activity.getUserId().toString());
            activity.setAct(Action.SUBSCRIBE_QUESTION);
        }
        transaction.exec();
        activities.addAll(subscribeQuestionActivities);
    }

    private void restoreSubscribeTopicActivities(Jedis jedis) {
        List<TopicActivity> subscribeTopicActivities = backupDao.selectSubscribeTopic();
        Transaction transaction = jedis.multi();
        for (TopicActivity topicActivity : subscribeTopicActivities) {
            double gmtCreate = topicActivity.getGmtCreate();
            Long userId = topicActivity.getUserId();
            String topic = topicActivity.getTopic();
            String topicKey = PREFIX_TOPIC + topic;
            String userTopicKey = PREFIX_USER + userId + SUFFIX_SUBSCRIBE_TOPIC;
            transaction.zadd(topicKey + SUFFIX_SUBSCRIBERS, gmtCreate, userId.toString());
            transaction.zadd(userTopicKey, gmtCreate, topic);
        }
        transaction.exec();
    }

    private void restoreApproveActivities(Jedis jedis) {
        List<Activity> approveActivities = backupDao.selectApprove();
        for (Activity activity : approveActivities) {
            jedis.zadd(PREFIX_REVIEW + activity.getId() + SUFFIX_APPROVERS, activity.getGmtCreate(), activity.getUserId().toString());
        }
    }
}
