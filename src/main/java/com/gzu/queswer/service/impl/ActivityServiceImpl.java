package com.gzu.queswer.service.impl;

import com.gzu.queswer.model.Action;
import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.vo.ActivityInfo;
import com.gzu.queswer.model.vo.QuestionInfo;
import com.gzu.queswer.model.vo.UserInfo;
import com.gzu.queswer.service.ActivityService;
import com.gzu.queswer.service.AnswerService;
import com.gzu.queswer.service.QuestionService;
import com.gzu.queswer.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.util.*;


@Service
@Slf4j
public class ActivityServiceImpl extends RedisService implements ActivityService {
    @Autowired
    AnswerService answerService;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;

    @Override
    public boolean saveActivity(Activity activity) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            res = saveActivity(activity, jedis);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    @Override
    public boolean saveActivity(Activity activity, Jedis jedis) {
        String key = PREFIX_ACTIVITIES + activity.getUserId();
        String member = activity.getAct() + ":" + activity.getId();
        return jedis.zadd(key, activity.getGmtCreate().doubleValue(), member).equals(1L);
    }

    @Override
    public boolean deleteActivity(Activity activity) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String key = PREFIX_ACTIVITIES + activity.getUserId();
            String member = activity.getAct() + ":" + activity.getId();
            res = jedis.zrem(key, member).equals(1L);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    @Override
    public List<ActivityInfo> queryPeopleActivities(Long peopleId, Long userId, int page, int limit) {
        int offset = limit * page;
        List<ActivityInfo> activityInfos;
        try (Jedis jedis = getJedis()) {
            String key = PREFIX_ACTIVITIES + peopleId;
            Set<Tuple> activityTuples = jedis.zrevrangeByScoreWithScores(key, Double.POSITIVE_INFINITY, 0.0, offset, limit);
            activityInfos = new ArrayList<>(activityTuples.size());
            for (Tuple activityTuple : activityTuples) {
                Activity activity = getActivity(activityTuple, peopleId);
                ActivityInfo activityInfo = getActivityInfo(activity, userId);
                if (activityInfo != null) {
                    activityInfo.setActivity(activity);
                    activityInfos.add(activityInfo);
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
            activityInfos = new ArrayList<>();
        }
        return activityInfos;
    }

    @Override
    public List<ActivityInfo> queryFollowActivities(Long userId, int page, int limit) {
        int offset = limit * page;
        List<ActivityInfo> activityInfos = new ArrayList<>(limit);
        try (Jedis jedis = getJedis()) {
            //判断是否已经加载到内存
            String tempKey = getTempKey(userId, jedis);
            String userIdKey = PREFIX_USER + userId;
            if (tempKey == null || page == 0) {
                //找到所有用户的id
                tempKey = userIdKey + SUFFIX_TEMP_ACTIVITIES;
                Set<String> peopleIdStrings = jedis.zrange(userIdKey + SUFFIX_F0LLOWS, 0L, -1L);
                List<Map<String, Double>> activityMaps = new ArrayList<>(16);
                for (String peopleIdString : peopleIdStrings) {
                    Set<Tuple> activityTuples = jedis.zrevrangeByScoreWithScores(PREFIX_ACTIVITIES + peopleIdString, Double.POSITIVE_INFINITY, 0.0);
                    for (Tuple activityTuple : activityTuples) {
                        Map<String, Double> map = new HashMap<>();
                        map.put(peopleIdString + ":" + activityTuple.getElement(), activityTuple.getScore());
                        activityMaps.add(map);
                    }
                }
                Transaction transaction = jedis.multi();
                for (Map<String, Double> map : activityMaps) transaction.zadd(tempKey, map);
                transaction.expire(tempKey, ONE_MINUTE);
                transaction.exec();
            }
            Set<Tuple> activityTuples = jedis.zrevrangeByScoreWithScores(tempKey, Double.POSITIVE_INFINITY, 0.0, offset, limit);
            for (Tuple activityTuple : activityTuples) {
                Activity activity = getActivity(activityTuple);
                ActivityInfo activityInfo = getActivityInfo(activity, userId);
                if (activityInfo != null) {
                    activityInfo.setUserInfo(userService.getUserInfo(activity.getUserId(), userId));
                    activityInfo.setActivity(activity);
                    activityInfos.add(activityInfo);
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return activityInfos;
    }

    private String getTempKey(Long userId, Jedis jedis) {
        String tempKey = PREFIX_USER + userId + SUFFIX_TEMP_ACTIVITIES;
        return jedis.expire(tempKey, ONE_MINUTE) != 0L ? tempKey : null;
    }

    private Activity getActivity(Tuple activityTuple, Long peopleId) {
        Activity activity = new Activity();
        activity.setUserId(peopleId);
        activity.setGmtCreate((long) activityTuple.getScore());
        String actvityKey = activityTuple.getElement();
        int signIndex = actvityKey.indexOf(':');
        activity.setAct(Short.parseShort(actvityKey.substring(0, signIndex)));
        activity.setId(Long.parseLong(actvityKey.substring(signIndex + 1)));
        return activity;
    }

    private Activity getActivity(Tuple activityTuple) {
        Activity activity = new Activity();
        activity.setGmtCreate((long) activityTuple.getScore());
        String[] actvityKeys = activityTuple.getElement().split(":");
        activity.setUserId(Long.parseLong(actvityKeys[0]));
        activity.setAct(Short.parseShort(actvityKeys[1]));
        activity.setId(Long.parseLong(actvityKeys[2]));
        return activity;
    }

    @Override
    public ActivityInfo getActivityInfo(Activity activity, Long userId) {
        Short act = activity.getAct();
        if (Action.FOLLOW_USER.equals(act))
            //0 关注了用户
            return getUserActivityInfo(activity, userId);
        else if (Action.SAVE_QUESTION.equals(act))
            //1 添加了问题（不匿名）
            return getQuestionActivityInfo(activity, userId, false);
        else if (Action.SUBSCRIBE_QUESTION.equals(act))
            //2 关注了问题
            return getQuestionActivityInfo(activity, userId, true);
        else if (Action.SAVE_ANSWER.equals(act))
            //4 回答了问题（不匿名）
            return getAnswerActivityInfo(activity, userId, false);
        else if (Action.ATTITUDE_ANSWER.equals(act) && answerService.getAgree(activity.getId(), activity.getUserId()))
            //5 赞同了回答
            return getAnswerActivityInfo(activity, userId, true);
        else
            return null;
    }

    private ActivityInfo getUserActivityInfo(Activity activity, Long userId) {
        UserInfo userInfo = userService.getUserInfo(activity.getId(), userId);
        if (userInfo == null) {
            deleteActivity(activity);
            return null;
        }
        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.setInfo(userInfo);
        return activityInfo;
    }

    private ActivityInfo getQuestionActivityInfo(Activity activity, Long userId, boolean subscribed) {
        QuestionInfo questionInfo = questionService.getQuestionInfo(activity.getId(), userId, false);
        if (questionInfo == null) {
            //问题被删除了
            deleteActivity(activity);
            return null;
        }
        if (subscribed || Boolean.FALSE.equals(questionInfo.getUserInfo().getAnonymous())) {
            //订阅或是不匿名添加问题
            ActivityInfo activityInfo = new ActivityInfo();
            activityInfo.setInfo(questionInfo);
            return activityInfo;
        } else
            return null;
    }

    private ActivityInfo getAnswerActivityInfo(Activity activity, Long userId, boolean agreed) {
        Long answerId = activity.getId();
        Answer answer = answerService.getAnswer(activity.getId());
        if (answer == null) {
            deleteActivity(activity);
            return null;
        }
        QuestionInfo questionInfo = questionService.getQuestionInfo(answer.getQuestionId(), answerId, userId, false, false);
        if (agreed || Boolean.FALSE.equals(answer.getAnonymous())) {
            ActivityInfo activityInfo = new ActivityInfo();
            activityInfo.setInfo(questionInfo);
            return activityInfo;
        } else
            return null;
    }
}
