package com.gzu.queswer.service;

import com.gzu.queswer.model.Activity;

import java.util.List;

/**
 * @author 蒋竟成
 * @date 2020/4/20
 */
public interface ActivityService {
    boolean saveActivity(Activity activity);

    boolean deleteActivity(Activity activity);

    List<Activity> selectActivitiesByUserId(Long userId, int offset, int limit);
}
