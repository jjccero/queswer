package com.gzu.queswer.service.impl;

import com.gzu.queswer.dao.CacheDao;
import com.gzu.queswer.model.Action;
import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.StringIndex;
import com.gzu.queswer.model.vo.QuestionInfo;
import com.gzu.queswer.model.vo.UserInfo;
import com.gzu.queswer.service.ActivityService;
import com.gzu.queswer.service.CacheService;
import com.gzu.queswer.service.QuestionService;
import com.gzu.queswer.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class CacheServiceImpl extends RedisService implements CacheService {
    @Autowired
    CacheDao cacheDao;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    ActivityService activityService;

    public boolean createIndex() {
        boolean res = false;
        try (Jedis jedis = getJedis(T_QUESTION_INDEX)) {
            jedis.flushDB();
            List<StringIndex> indexs = cacheDao.selectQuestionIndexs();
            for (StringIndex stringIndex : indexs) {
                jedis.sadd(stringIndex.getK().toLowerCase(), stringIndex.getV().toString());
            }
            jedis.select(T_USER_INDEX);
            jedis.flushDB();
            indexs = cacheDao.selectUserIndexs();
            for (StringIndex stringIndex : indexs) {
                jedis.sadd(stringIndex.getK().toLowerCase(), stringIndex.getV().toString());
            }
            res = true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
    public List<QuestionInfo> selectQuestionInfosByQuestion(String title, Long userId) {
        List<Long> qIds = selectIndex(title, T_QUESTION_INDEX);
        List<QuestionInfo> questionInfos = new ArrayList<>(qIds.size());
        for (Long qid : qIds) {
            questionInfos.add(questionService.getQuestionInfo(qid, userId, false));
        }
        return questionInfos;
    }

    @Override
    public List<UserInfo> selectUserInfosByNickname(String nickname, Long userId) {
        List<Long> peopleIds = selectIndex(nickname, T_USER_INDEX);
        List<UserInfo> userInfos = new ArrayList<>(peopleIds.size());
        for (Long peopleId : peopleIds) {
            userInfos.add(userService.getUserInfo(peopleId, userId));
        }
        return userInfos;
    }

    @Override
    public boolean flush() {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            jedis.flushAll();
            res = true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
    public boolean backup() {
        try (Jedis jedis = getJedis()) {
            List<Long> qIds = cacheDao.selectQuestionIds();
            for (Long qId : qIds) {
                jedis.zadd(TOP_LIST_KEY, 0.0, qId.toString());
                List<Long> aIds = cacheDao.selectAnswerIdsByQuestionId(qId);
                String qIdKey = PREFIX_QUESTION + qId + SUFFIX_ANSWERS;
                for (Long aId : aIds) {
                    jedis.zadd(qIdKey, 0.0, aId.toString());
                    List<Long> rIds = cacheDao.selectReviewIdsByAnswerId(aId);
                    String aIdKey = PREFIX_ANSWER + aId + SUFFIX_REVIEWS;
                    for (Long rId : rIds) {
                        jedis.zadd(aIdKey, 0.0, rId.toString());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean restore() {
        try (Jedis jedis = getJedis()) {
            long startTime = System.currentTimeMillis();
            cacheDao.deleteActivities();
            logTime(startTime, "deleteActivities");
            jedis.flushAll();
            List<Long> qIds = cacheDao.selectQuestionIds();
            for (Long qId : qIds) {
                jedis.zadd(TOP_LIST_KEY, 0.0, qId.toString());
                List<Long> aIds = cacheDao.selectAnswerIdsByQuestionId(qId);
                String qIdKey = PREFIX_QUESTION + qId + SUFFIX_ANSWERS;
                for (Long aId : aIds) {
                    jedis.zadd(qIdKey, 0.0, aId.toString());
                    List<Long> rIds = cacheDao.selectReviewIdsByAnswerId(aId);
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
            restoreSubscribeTopicActivities(activities);
            logTime(startTime, "restoreSubscribeTopicActivities");
            //恢复评论赞同表,不需要加入时间轴
            restoreApproveActivities(jedis);
            logTime(startTime, "restoreApproveActivities");
//            log.vo("{}",cacheDao.insertActivityBatch(activities));
            for (Activity activity : activities) {
                activityService.saveActivity(activity, jedis);
            }
            logTime(startTime, "insertActivityBatch");
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    private void logTime(long startTime, String msg) {
        log.info("{}:{}ms", msg, System.currentTimeMillis() - startTime);
    }

    private Jedis getJedis(int database) {
        Jedis jedis = super.getJedis();
        jedis.select(database);
        return jedis;
    }

    private List<Long> selectIndex(String k, int database) {
        List<Long> ids = new ArrayList<>();
        try (Jedis jedis = getJedis(database)) {
            String cursor = ScanParams.SCAN_POINTER_START;
            String match = "*" + k.toLowerCase() + "*";
            ScanParams scanParams = new ScanParams();
            scanParams.match(match);
            scanParams.count(100);
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                cursor = scanResult.getCursor();
                List<String> indexKeys = scanResult.getResult();
                for (String indexKey : indexKeys) {
                    Set<String> keys = jedis.smembers(indexKey);
                    for (String key : keys) {
                        ids.add(Long.parseLong(key));
                    }
                }
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ids;
    }

    private void restoreAnswerActivities(List<Activity> activities) {
        List<Activity> answerActivities = cacheDao.selectAnswerActivities();
        for (Activity activity : answerActivities) {
            activity.setAct(Action.SAVE_ANSWER);
        }
        activities.addAll(answerActivities);
    }

    private void restoreQuestionActivities(List<Activity> activities) {
        List<Activity> questionActivities = cacheDao.selectQuestionActivities();
        for (Activity activity : questionActivities) {
            activity.setAct(Action.SAVE_QUESTION);
        }
        activities.addAll(questionActivities);
    }

    private List<Activity> restoreAttitudeActivities(long startTime, Jedis jedis) {
        List<Attitude> attitudes = cacheDao.selectAttitudes();
        List<Activity> activities = new ArrayList<>(attitudes.size());
        logTime(startTime, "activities");
        for (Attitude attitude : attitudes) {
            Transaction transaction = jedis.multi();
            String userIdField = attitude.getUserId().toString();
            if (Boolean.TRUE.equals(attitude.getAtti())) {
                transaction.srem(PREFIX_ANSWER + attitude.getAnswerId() + SUFFIX_AGAINST, userIdField);
                transaction.sadd(PREFIX_ANSWER + attitude.getAnswerId() + SUFFIX_AGREE, userIdField);
            } else {
                transaction.srem(PREFIX_ANSWER + attitude.getAnswerId() + SUFFIX_AGREE, userIdField);
                transaction.sadd(PREFIX_ANSWER + attitude.getAnswerId() + SUFFIX_AGAINST, userIdField);
            }
            transaction.exec();
            Activity activity = new Activity();
            activity.setUserId(attitude.getUserId());
            activity.setId(attitude.getAnswerId());
            activity.setAct(Action.ATTITUDE_ANSWER);
            activity.setGmtCreate(attitude.getGmtCreate());
            activities.add(activity);
        }
        return activities;
    }

    private void restoreSubscribeQuestionActivities(Jedis jedis, List<Activity> activities) {
        List<Activity> subscribeQuestionActivities = cacheDao.selectSubscribeQuestionActivities();
        for (Activity activity : subscribeQuestionActivities) {
            jedis.sadd(PREFIX_QUESTION + activity.getId() + SUFFIX_SUBSCRIBERS, activity.getUserId().toString());
            activity.setAct(Action.SUBSCRIBE_QUESTION);
        }
        activities.addAll(subscribeQuestionActivities);
    }

    private void restoreSubscribeTopicActivities(List<Activity> activities) {
        List<Activity> subscribeTopicActivities = cacheDao.selectSubscribeTopicActivities();
        for (Activity activity : subscribeTopicActivities) {
            activity.setAct(Action.SUBSCRIBE_TOPIC);
        }
        activities.addAll(subscribeTopicActivities);
    }

    private void restoreApproveActivities(Jedis jedis) {
        List<Activity> approveActivities = cacheDao.selectApproveActivities();
        for (Activity activity : approveActivities) {
            jedis.sadd(PREFIX_REVIEW + activity.getId() + SUFFIX_APPROVERS, activity.getUserId().toString());
        }
    }
}
