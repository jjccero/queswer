package com.gzu.queswer.service.impl;

import com.gzu.queswer.dao.CacheDao;
import com.gzu.queswer.model.StringIndex;
import com.gzu.queswer.model.info.QuestionInfo;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.service.CacheService;
import com.gzu.queswer.service.QuestionService;
import com.gzu.queswer.service.UserService;
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
public class CacheServiceImpl extends RedisService implements CacheService {
    @Autowired
    CacheDao cacheDao;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;

    public boolean createIndex() {
        boolean res = false;
        try (Jedis jedis = getJedis(T_QUESTION_INDEX)) {
            jedis.flushDB();
            List<StringIndex> indexs = cacheDao.selectQuestionIndexs();
            for (StringIndex stringIndex : indexs) {
                jedis.sadd(stringIndex.getK().toLowerCase(), stringIndex.getV().toString());
            }
            jedis.select(T_USER_INDEX);
            jedis.flushDB();
            indexs = cacheDao.selectUserIndexs();
            for (StringIndex stringIndex : indexs) {
                jedis.sadd(stringIndex.getK().toLowerCase(), stringIndex.getV().toString());
            }
            res = true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    public boolean initRedis() {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            jedis.flushDB();
            List<Long> qIds = cacheDao.selectQIds();
            for (Long qId : qIds) {
                jedis.zadd(TOP_LIST_KEY, 0.0, qId.toString());
                List<Long> aIds = cacheDao.selectAIdsByQId(qId);
                String qIdKey = PREFIX_QUESTION + qId + SUFFIX_ANSWERS;
                for (Long aId : aIds) {
                    jedis.zadd(qIdKey, 0.0, aId.toString());
                    List<Long> rIds = cacheDao.selectRIdsByAId(aId);
                    String aIdKey = PREFIX_ANSWER + aId + SUFFIX_REVIEWS;
                    for (Long rId : rIds) {
                        jedis.zadd(aIdKey, 0.0, rId.toString());
                    }
                }
            }
            res = true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
    public List<QuestionInfo> selectQuestionInfosByQuestion(String title, Long uId) {
        List<Long> qIds = selectQidsByQuestion(title);
        List<QuestionInfo> questionInfos = new ArrayList<>(qIds.size());
        for (Long qid : qIds) {
            questionInfos.add(questionService.getQuestionInfo(qid, uId, false));
        }
        return questionInfos;
    }

    @Override
    public List<UserInfo> selectUserInfosByNickname(String nickname, Long uId) {
        List<Long> peopleIds = selectUIdsByNickname(nickname);
        List<UserInfo> userInfos = new ArrayList<>(peopleIds.size());
        for (Long peopleId : peopleIds) {
            userInfos.add(userService.getUserInfo(peopleId, uId));
        }
        return userInfos;
    }

    private List<Long> selectUIdsByNickname(String nickname) {
        return selectIndex(nickname, T_USER_INDEX);
    }

    private List<Long> selectQidsByQuestion(String question) {
        return selectIndex(question, T_QUESTION_INDEX);
    }

    private Jedis getJedis(int database) {
        Jedis jedis = super.getJedis();
        jedis.select(database);
        return jedis;
    }

    private List<Long> selectIndex(String k, int database) {
        List<Long> ids = new ArrayList<>();
        try (Jedis jedis = getJedis(database)) {
            String cursor = ScanParams.SCAN_POINTER_START;
            String match = "*" + k.toLowerCase() + "*";
            ScanParams scanParams = new ScanParams();
            scanParams.match(match);
            scanParams.count(100);
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                cursor = scanResult.getCursor();
                List<String> indexKeys = scanResult.getResult();
                for (String indexKey : indexKeys) {
                    Set<String> keys = jedis.smembers(indexKey);
                    for (String key : keys) {
                        ids.add(Long.parseLong(key));
                    }
                }
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ids;
    }
}
