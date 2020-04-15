package com.gzu.queswer.dao.impl;

import com.gzu.queswer.dao.CacheDao;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.model.StringIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;

@Repository
public class CacheDaoImpl extends RedisDao {
    @Autowired
    CacheDao cacheDao;
    public boolean createIndex(){
        boolean res=false;
        Jedis jedis = null;
        try {
            jedis = getJedis(t_question_index);
            jedis.flushDB();
            List<StringIndex> indexs=cacheDao.selectQuestionIndexs();
            for(StringIndex stringIndex :indexs){
                jedis.sadd(stringIndex.getK().toLowerCase(), stringIndex.getV().toString());
            }
            jedis.select(t_user_index);
            jedis.flushDB();
            indexs=cacheDao.selectUserIndexs();
            for(StringIndex stringIndex :indexs){
                jedis.sadd(stringIndex.getK().toLowerCase(), stringIndex.getV().toString());
            }
            res=true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }
    public List<Long> selectQidsByQuestion(String question){
        return selectIndex(question,t_question_index);
    }
    public List<Long> selectIndex(String k,int database){
        List<Long> ids=new ArrayList<>();
        Jedis jedis = null;
        try {
            jedis = getJedis(database);
            String cursor= ScanParams.SCAN_POINTER_START;
            String match="*"+k.toLowerCase()+"*";
            ScanParams scanParams=new ScanParams();
            scanParams.match(match);
            scanParams.count(100);
            do{
                ScanResult<String> scanResult=jedis.scan(cursor,scanParams);
                cursor=scanResult.getCursor();
                List<String> indexKeys=scanResult.getResult();
                for(String indexKey:indexKeys){
                    Set<String> keys=jedis.smembers(indexKey);
                    for(String key:keys){
                        ids.add(Long.parseLong(key));
                    }
                }
            }while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return ids;
    }
    public List<Long> selectUserInfosByNickname(String nickname){
        return selectIndex(nickname,t_user_index);
    }
    public Jedis getJedis(int database) {
        Jedis jedis = super.getJedis();
        jedis.select(database);
        return jedis;
    }
}
