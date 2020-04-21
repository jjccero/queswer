package com.gzu.queswer.service;

import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.info.ActivityInfo;

import java.util.List;

/**
 * @author 蒋竟成
 * @date 2020/4/20
 */
public interface ActivityService {

    boolean saveActivity(Activity activity);

    boolean deleteActivity(Activity activity);

    List<ActivityInfo> queryPeopleActivities(Long peopleId, Long userId, int offset, int limit);
}
