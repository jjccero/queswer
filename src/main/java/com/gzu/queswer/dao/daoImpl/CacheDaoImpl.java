package com.gzu.queswer.dao.daoImpl;

import com.gzu.queswer.dao.CacheDao;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.model.QuestionIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class CacheDaoImpl extends RedisDao {
    @Autowired
    CacheDao cacheDao;
    public void createIndex(){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.flushDB();
            List<QuestionIndex> questionIndices=cacheDao.selectQuestionIndexs();
            for(QuestionIndex questionIndex:questionIndices){
                jedis.sadd(questionIndex.getQuestion(),questionIndex.getQid().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }
    public List<Long> selectQidsByQuestion(String question){
        List<Long> qids=new ArrayList<>();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Set<String> questionIndexKeys= jedis.keys("*"+question+"*");
            for(String questionIndexKey:questionIndexKeys){
                Set<String> qidKeys=jedis.smembers(questionIndexKey);
                for(String qidKey:qidKeys){
                    qids.add(Long.parseLong(qidKey));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return qids;
    }
    @Override
    public Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(t_question_index);
        return jedis;
    }
}
