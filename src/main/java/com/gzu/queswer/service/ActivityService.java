package com.gzu.queswer.service;

import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.vo.ActivityInfo;
import redis.clients.jedis.Jedis;

import java.util.List;


public interface ActivityService {

    boolean saveActivity(Activity activity);

    boolean deleteActivity(Activity activity);

    List<ActivityInfo> queryPeopleActivities(Long peopleId, Long userId, int offset, int limit);

    List<ActivityInfo> queryFollowActivities(Long userId, int page, int pageSize);

    boolean saveActivity(Activity activity, Jedis jedis);

    ActivityInfo getActivityInfo(Activity activity, Long userId);
}
