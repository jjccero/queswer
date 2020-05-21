package com.gzu.queswer.dao;

import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.Attitude;
import com.gzu.queswer.model.TopicActivity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackupDao {
    List<Long> selectQuestionIds();

    List<Long> selectAnswerIdsByQuestionId(Long questionId);

    List<Long> selectReviewIdsByAnswerId(Long answerId);

    List<Activity> selectQuestionActivities();

    List<Activity> selectAnswerActivities();

    List<Attitude> selectAttitudes();

    List<Activity> selectSubscribeQuestion();

    List<Activity> selectApprove();

    List<TopicActivity> selectSubscribeTopic();
}
