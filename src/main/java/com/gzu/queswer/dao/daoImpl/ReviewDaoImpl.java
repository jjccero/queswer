package com.gzu.queswer.dao.daoImpl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.dao.ReviewDao;
import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.info.ReviewInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

@Repository
public class ReviewDaoImpl extends RedisDao {
    @Autowired
    private ReviewDao reviewDao;

    public Long insertReview(Review review) {
        reviewDao.insertReview(review);
        Long rid = review.getRid();
        if (rid != null) {
            Jedis jedis = null;
            try {
                jedis = getJedis();
                jedis.set(rid.toString(), JSON.toJSONString(review), setParams_30m);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jedis != null)
                    jedis.close();
            }
        }
        return rid;
    }


    public boolean deleteReviewByUid(Long rid, Long uid) {
        boolean res = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String rid_key = getKey(rid, jedis);
            if (rid_key != null) {
                Review review=getReview(rid_key,jedis);
                if(review.getUid().equals(uid)){
                    review.setReview(null);
                    review.setDeleted(true);
                    jedis.set(rid_key,JSON.toJSONString(review),setParams_30m);
                    reviewDao.deleteReviewByRid(rid);
                    res = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public boolean updateApprove(Long rid, Long uid, Boolean approve) {
        boolean res = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String rid_key = getKey(rid, jedis);
            if (rid_key != null) {
                if (approve) jedis.sadd(rid_key + ":a", uid.toString());
                else jedis.srem(rid_key + ":a", uid.toString());
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public ReviewInfo getReviewInfo(Long rid, Long uid) {
        ReviewInfo reviewInfo = new ReviewInfo();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String rid_key = getKey(rid, jedis);
            if (rid_key != null) {
                Review review = getReview(rid_key, jedis);
                String rid_a_key = rid_key + ":a";
                reviewInfo.setApproveCount(jedis.scard(rid_a_key));
                if (uid != null) {
                    reviewInfo.setApproved(jedis.sismember(rid_a_key, uid.toString()));
                }
                reviewInfo.setReview(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return reviewInfo;
    }

    public Review getReview(String rid_key, Jedis jedis) {
        return JSON.parseObject(jedis.get(rid_key), Review.class);
    }

    @Value("${t_review}")
    int database;

    @Override
    public String getKey(Long rid, Jedis jedis) {
        String rid_key = rid.toString();
        if (jedis.expire(rid_key, second_30m) == 0L) {
            Review review = reviewDao.selectReviewByRid(rid);
            jedis.set(rid_key, review != null ? JSON.toJSONString(review) : "", setParams_30m);
        }
        return jedis.strlen(rid_key) == 0L ? null : rid_key;
    }

    @Override
    protected Jedis getJedis() {
        Jedis jedis = super.getJedis();
        jedis.select(database);
        return jedis;
    }
}
