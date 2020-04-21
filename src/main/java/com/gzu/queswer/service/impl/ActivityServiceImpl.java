package com.gzu.queswer.service.impl;

import com.gzu.queswer.dao.ActivityDao;
import com.gzu.queswer.model.Activity;
import com.gzu.queswer.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 蒋竟成
 * @date 2020/4/20
 */
@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    ActivityDao activityDao;

    @Override
    public boolean saveActivity(Activity activity) {
        return activityDao.insertActivity(activity) == 1;
    }

    @Override
    public boolean deleteActivity(Activity activity) {
        return activityDao.deleteActivity(activity) == 1;
    }

    @Override
    public List<Activity> selectActivitiesByUserId(Long userId, int offset, int limit) {
        return activityDao.selectActivitiesByUserId(userId, offset, limit);
    }
}
