package com.gzu.queswer.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.dao.SearchDao;
import com.gzu.queswer.model.StringIndex;
import com.gzu.queswer.model.vo.QuestionInfo;
import com.gzu.queswer.model.vo.UserInfo;
import com.gzu.queswer.service.QuestionService;
import com.gzu.queswer.service.SearchService;
import com.gzu.queswer.service.UserService;
import com.gzu.queswer.util.AnalysisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class SearchServiceImpl extends RedisService implements SearchService {
    @Autowired
    SearchDao searchDao;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;

    public boolean createIndex() {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            //建立问题索引
            jedis.select(T_QUESTION_INDEX);
            jedis.flushDB();
            List<StringIndex> indexs = searchDao.selectQuestionIndexs();
            for (StringIndex stringIndex : indexs) {
                Set<String> templates = AnalysisUtil.analysisString(stringIndex.getK());
                String questionIdString = stringIndex.getV().toString();
                for (String string : templates) {
                    jedis.sadd(string, questionIdString);
                }
            }
            //建立用户索引
            jedis.select(T_USER_INDEX);
            jedis.flushDB();
            indexs = searchDao.selectUserIndexs();
            for (StringIndex stringIndex : indexs) {
                jedis.sadd(stringIndex.getK().toLowerCase(), stringIndex.getV().toString());
            }
            res = true;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    @Override
    public JSONObject selectQuestionInfosByQuestion(String title, Long userId) {
        JSONObject res = new JSONObject();
        try (Jedis jedis = getJedis()) {
            jedis.select(T_QUESTION_INDEX);
            //分词结果保存到set
            Set<String> templates = AnalysisUtil.analysisString(title);
            if (!templates.isEmpty()) {
                //使用redis求并集
                Set<String> questionIdStrings = jedis.sunion(templates.toArray(new String[0]));
                List<QuestionInfo> questionInfos = new ArrayList<>(questionIdStrings.size());
                for (String questionIdString : questionIdStrings) {
                    Long questionId = Long.parseLong(questionIdString);
                    questionInfos.add(questionService.getQuestionInfo(questionId, userId, false));
                }
                //返回添加问题
                res.put("questionInfos", questionInfos);
            }
            //返回添加词
            res.put("templates", templates);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    @Override
    public List<UserInfo> selectUserInfosByNickname(String nickname, Long userId) {
        List<UserInfo> userInfos = new ArrayList<>(8);
        try (Jedis jedis = getJedis()) {
            jedis.select(T_USER_INDEX);
            String cursor = ScanParams.SCAN_POINTER_START;
            String match = nickname.toLowerCase() + "*";
            ScanParams scanParams = new ScanParams();
            scanParams.match(match);
            scanParams.count(100);
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                cursor = scanResult.getCursor();
                List<String> indexKeys = scanResult.getResult();
                for (String indexKey : indexKeys) {
                    Set<String> userIdStrings = jedis.smembers(indexKey);
                    for (String userIdString : userIdStrings) {
                        userInfos.add(userService.getUserInfo(Long.parseLong(userIdString), userId));
                    }
                }
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        } catch (Exception e) {
            log.error(e.toString());
        }
        return userInfos;
    }
}
