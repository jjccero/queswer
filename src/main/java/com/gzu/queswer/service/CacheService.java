package com.gzu.queswer.service;

import com.gzu.queswer.model.info.QuestionInfo;
import com.gzu.queswer.model.info.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CacheService {
    boolean createIndex();

    boolean initRedis();

    List<QuestionInfo> selectQuestionInfosByQuestion(String title, Long uId);

    List<UserInfo> selectUserInfosByNickname(String nickname, Long uId);
}
