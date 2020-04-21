package com.gzu.queswer.dao;

import com.gzu.queswer.model.Activity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityDao {
    int insertActivity(Activity activity);

    int deleteActivity(Activity activity);

    List<Activity> selectActivitiesByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
}
