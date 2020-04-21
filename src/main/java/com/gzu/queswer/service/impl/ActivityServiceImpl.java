package com.gzu.queswer.service.impl;

import com.gzu.queswer.dao.ActivityDao;
import com.gzu.queswer.model.Action;
import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.info.ActivityInfo;
import com.gzu.queswer.model.info.AnswerInfo;
import com.gzu.queswer.model.info.QuestionInfo;
import com.gzu.queswer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    AnswerService answerService;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    TopicService topicService;

    @Override
    public List<ActivityInfo> queryPeopleActivities(Long peopleId, Long userId, int offset, int limit) {
        List<Activity> activities = activityDao.selectActivitiesByUserId(peopleId, offset, limit);
        List<ActivityInfo> activityInfos = new ArrayList<>(activities.size());
        for (Activity activity : activities) {
            ActivityInfo activityInfo = getActivityInfo(activity, userId);
            if (activityInfo != null) {
                activityInfo.setActivity(activity);
                activityInfos.add(activityInfo);
            }
        }
        return activityInfos;
    }

    private ActivityInfo getActivityInfo(Activity activity, Long userId) {
        ActivityInfo activityInfo = new ActivityInfo();
        Short act = activity.getAct();
        if (Action.FOLLOW_USER.equals(act))
            //0 关注了用户
            activityInfo.setInfo(userService.getUserInfo(activity.getId(), userId));
        else if (Action.SAVE_QUESTION.equals(act)) {
            QuestionInfo questionInfo = questionService.getQuestionInfo(activity.getId(), userId, false);
            //1 添加了问题（不匿名）
            if (Boolean.FALSE.equals(questionInfo.getUserInfo().getAnonymous()))
                activityInfo.setInfo(questionInfo);
            else
                activityInfo = null;
        } else if (Action.SUBSCRIBE_QUESTION.equals(act))
            //2 关注了问题
            activityInfo.setInfo(questionService.getQuestionInfo(activity.getId(), userId, false));
        else if (Action.SUBSCRIBE_TOPIC.equals(act))
            //3 关注了话题
            activityInfo.setInfo(topicService.getTopic(activity.getId()));
        else if (Action.SAVE_ANSWER.equals(act)) {
            AnswerInfo answerInfo = answerService.getAnswerInfo(activity.getId(), userId);
            //4 回答了问题（不匿名）
            if (Boolean.FALSE.equals(answerInfo.getUserInfo().getAnonymous()))
                activityInfo.setInfo(answerInfo);
            else
                activityInfo = null;
        } else if (Action.ATTITUDE_ANSWER.equals(act) && answerService.getAgree(activity.getId(), activity.getUserId())) {
            //5 赞同了回答
            activityInfo.setInfo(answerService.getAnswerInfo(activity.getId(), userId));
        }
        return activityInfo;
    }
}
