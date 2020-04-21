package com.gzu.queswer.service.impl;

import com.gzu.queswer.dao.CacheDao;
import com.gzu.queswer.model.Action;
import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.StringIndex;
import com.gzu.queswer.model.info.QuestionInfo;
import com.gzu.queswer.model.info.UserInfo;
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
            long startTime=System.currentTimeMillis();
            cacheDao.deleteActivities();
            logTime(startTime,"deleteActivities");
            jedis.flushDB();
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
            logTime(startTime,"flushDB");
            //提问加入时间轴
            restoreQuestionActivities();
            logTime(startTime,"restoreQuestionActivities");
            //回答加入时间轴
            restoreAnswerActivities();
            logTime(startTime,"restoreAnswerActivities");
            //恢复态度表，并加入时间轴
            restoreAttitudeActivities(jedis);
            logTime(startTime,"restoreAttitudeActivities");
            //恢复问题订阅表，并加入时间轴
            restoreSubscribeQuestionActivities(jedis);
            logTime(startTime,"restoreSubscribeQuestionActivities");
            //恢复话题订阅情况加入时间轴
            restoreSubscribeTopicActivities();
            logTime(startTime,"restoreSubscribeTopicActivities");
            //恢复评论赞同表,不需要加入时间轴
            restoreApproveActivities(jedis);
            logTime(startTime,"restoreApproveActivities");
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

    private void restoreAnswerActivities() {
        List<Activity> answerActivities = cacheDao.selectAnswerActivities();
        for (Activity activity : answerActivities) {
            activity.setAct(Action.SAVE_ANSWER);
            activityService.saveActivity(activity);
        }
    }

    private void restoreQuestionActivities() {
        List<Activity> questionActivities = cacheDao.selectQuestionActivities();
        for (Activity activity : questionActivities) {
            activity.setAct(Action.SAVE_QUESTION);
            activityService.saveActivity(activity);
        }
    }

    private void restoreAttitudeActivities(Jedis jedis) {
        List<Attitude> attitudes = cacheDao.selectAttitudes();
        for (Attitude attitude : attitudes) {
            if (Boolean.TRUE.equals(attitude.getAtti()))
                jedis.sadd(PREFIX_ANSWER + attitude.getAnswerId() + SUFFIX_AGREE, attitude.getUserId().toString());
            else
                jedis.sadd(PREFIX_ANSWER + attitude.getAnswerId() + SUFFIX_AGAINST, attitude.getUserId().toString());
            Activity activity = new Activity();
            activity.setUserId(attitude.getUserId());
            activity.setId(attitude.getAnswerId());
            activity.setAct(Action.ATTITUDE_ANSWER);
            activity.setGmtCreate(attitude.getGmtCreate());
            activityService.saveActivity(activity);
        }
    }

    private void restoreSubscribeQuestionActivities(Jedis jedis) {
        List<Activity> subscribeQuestionActivities = cacheDao.selectSubscribeQuestionActivities();
        for (Activity activity : subscribeQuestionActivities) {
            jedis.sadd(PREFIX_QUESTION + activity.getId() + SUFFIX_SUBSCRIBERS, activity.getUserId().toString());
            activity.setAct(Action.SUBSCRIBE_QUESTION);
            activityService.saveActivity(activity);
        }
    }

    private void restoreSubscribeTopicActivities() {
        List<Activity> subscribeTopicActivities = cacheDao.selectSubscribeTopicActivities();
        for (Activity activity : subscribeTopicActivities) {
            activity.setAct(Action.SUBSCRIBE_TOPIC);
            activityService.saveActivity(activity);
        }
    }

    private void restoreApproveActivities(Jedis jedis) {
        List<Activity> approveActivities = cacheDao.selectApproveActivities();
        for (Activity activity : approveActivities) {
            jedis.sadd(PREFIX_REVIEW + activity.getId() + SUFFIX_APPROVERS, activity.getUserId().toString());
        }
    }
}
