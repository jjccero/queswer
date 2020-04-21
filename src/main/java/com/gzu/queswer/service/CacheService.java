package com.gzu.queswer.service;

import com.gzu.queswer.model.info.QuestionInfo;
import com.gzu.queswer.model.info.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CacheService {
    boolean createIndex();

    List<QuestionInfo> selectQuestionInfosByQuestion(String title, Long userId);

    List<UserInfo> selectUserInfosByNickname(String nickname, Long userId);

    boolean flush();

    boolean backup();

    boolean restore();
}
