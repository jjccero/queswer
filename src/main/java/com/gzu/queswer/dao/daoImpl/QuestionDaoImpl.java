package com.gzu.queswer.dao.daoImpl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.QuestionDao;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.info.QuestionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class QuestionDaoImpl extends RedisDao {
    @Autowired
    private QuestionDao questionDao;

    private Question getQuestion(String qid_key, Jedis jedis) {
        return JSON.parseObject(jedis.get(qid_key), Question.class);
    }

    public Question selectQuestionByQid(Long qid) {
        Jedis jedis = null;
        Question question = null;
        try {
            jedis = getJedis();
            String qid_key = getKey(qid, jedis);
            if (qid_key != null) {
                question = getQuestion(qid_key, jedis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return question;
    }

    public List selectAidsByQid(Long qid) {
        List aids = new ArrayList();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String qid_key = getKey(qid, jedis);
            if (qid_key != null) {
                String qid_a_key = qid_key + ":a";
                Set<String> aid_keys = jedis.zrange(qid_a_key, 0L, -1L);
                for (String aid_key : aid_keys) {
                    aids.add(Long.parseLong(aid_key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return aids;
    }

    public void insertQuestion(Question question) {
        questionDao.insertQuestion(question);
        if (question.getQid() != null) {
            Jedis jedis = null;
            try {
                jedis = getJedis();
                String qid_key = question.getQid().toString();
                jedis.zadd(TOP_LIST_KEY, 0.0, qid_key);
                jedis.set(qid_key, JSON.toJSONString(question), setParams_30m);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jedis != null)
                    jedis.close();
            }
        }
    }

    public List selectQuestions(int offset, int limit) {
        List<Question> questions = new ArrayList<>();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Set<String> qids = jedis.zrange(TOP_LIST_KEY, offset, offset + limit);
            for (String qid_key : qids) {
                getKey(Long.parseLong(qid_key), jedis);
                Question question = getQuestion(qid_key, jedis);
                questions.add(JSON.parseObject(jedis.get(qid_key), Question.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return questions;
    }

    public Integer selectFollowCount(Long qid) {
        return questionDao.selectFollowCount(qid);
    }

    private boolean getFollowed(String qid_f_key, String uid_key, Jedis jedis) {
        return jedis.sismember(qid_f_key, uid_key);
    }

    private long getFollowCount(String qid_f_key, Jedis jedis) {
        return jedis.scard(qid_f_key);
    }

    private double getViewCount(String qid_key, Jedis jedis) {
        return jedis.zscore(TOP_LIST_KEY, qid_key);
    }

    public QuestionInfo getQuestionInfo(Long qid, Long uid) {
        QuestionInfo questionInfo = new QuestionInfo();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String qid_key = getKey(qid, jedis);
            if (qid_key != null) {
                jedis.zincrby(TOP_LIST_KEY, 1.0, qid_key);
                Question question = getQuestion(qid_key, jedis);
                questionInfo.setQuestion(question);
                String qid_f_key = qid_key + ":f";
                questionInfo.setFollowCount(getFollowCount(qid_f_key, jedis));
                questionInfo.setViewCount(getViewCount(qid_key, jedis));
                questionInfo.setQuestioned(question.getUid().equals(uid));
                if (uid != null) {
                    questionInfo.setFollowed(getFollowed(qid_f_key, uid.toString(), jedis));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return questionInfo;
    }

    public Integer insertFollow(Long qid, Long uid) {
        int res = 0;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String qid_key = getKey(qid, jedis);
            if (qid_key != null) {
                jedis.sadd(qid_key + ":f", uid.toString());
                questionDao.insertFollow(qid, uid);
                res = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public Integer deleteFollow(Long qid, Long uid) {
        int res = 0;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String qid_key = getKey(qid, jedis);
            if (qid_key != null) {
                jedis.srem(qid_key + ":f", uid.toString());
                questionDao.deleteFollow(qid, uid);
                res = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public void insertAnswer(String qid_key, String aid_key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.zadd(qid_key + ":a", 0.0, aid_key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public boolean deleteAnswer(String qid_key, String aid_key) {
        boolean res = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            res = jedis.zrem(qid_key + ":a", aid_key) == 1L;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public List selectFollowsByUid(Long uid) {
        return null;
    }

    @Value("${t_question}")
    int database;
    final static String TOP_LIST_KEY = "t";

    //    final static String VIEW_LIST_KEY = "v";
    @Override
    protected Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(database);
        return jedis;
    }

    @Override
    public String getKey(Long qid, Jedis jedis) {
        String qid_key = qid.toString();
        if (jedis.expire(qid_key, second_30m) == 0L) {
            Question question = questionDao.selectQuestionByQid(qid);
            jedis.set(qid_key, question != null ? JSON.toJSONString(question) : "", setParams_30m);
        }
        return jedis.strlen(qid_key) == 0L ? null : qid_key;
    }

    public Answer selectAnswerByUid(Long qid, Long uid) {
        return questionDao.selectAnswerByUid(qid, uid);
    }
}
