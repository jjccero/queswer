package com.gzu.queswer.dao;

import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.StringIndex;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CacheDao {
    List<Long> selectQuestionIds();

    List<Long> selectAnswerIdsByQuestionId(Long qId);

    List<Long> selectReviewIdsByAnswerId(Long answerId);

    List<StringIndex> selectQuestionIndexs();

    List<StringIndex> selectUserIndexs();

    int deleteActivities();

    List<Activity> selectAnswerActivities();

    List<Activity> selectQuestionActivities();

    List<Attitude> selectAttitudes();

    List<Activity> selectSubscribeQuestionActivities();

    List<Activity> selectSubscribeTopicActivities();

    List<Activity> selectActivities();

    List<Activity> selectApproveActivities();

}
