package com.gzu.queswer.service;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.vo.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SearchService {
    boolean createIndex();

    JSONObject selectQuestionInfosByQuestion(String title, Long userId);

    List<UserInfo> selectUserInfosByNickname(String nickname, Long userId);
}
