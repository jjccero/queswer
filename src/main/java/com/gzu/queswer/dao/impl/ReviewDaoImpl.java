package com.gzu.queswer.dao.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.RedisDao;
import com.gzu.queswer.dao.ReviewDao;
import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.info.ReviewInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

@Repository
@Slf4j
public class ReviewDaoImpl extends RedisDao {
    @Autowired
    private ReviewDao reviewDao;

    public Long insertReview(Review review) {
        reviewDao.insertReview(review);
        Long rid = review.getrId();
        if (rid != null) {
            Jedis jedis = null;
            try {
                String rIdKey = rid.toString();
                jedis = getJedis();
                jedis.set(rIdKey, JSON.toJSONString(review), SET_PARAMS_THIRTY_MINUTES);
                jedis.zadd(PREFIX_ANSWER + review.getaId().toString() + SUFFIX_REVIEWS, 0.0, rIdKey);
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                if (jedis != null)
                    jedis.close();
            }
        }
        return rid;
    }


    public boolean deleteReviewByUid(Long rid, Long uid) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String rIdKey = getKey(rid, jedis);
            if (rIdKey != null) {
                Review review = getReview(rIdKey, jedis);
                if (review.getuId().equals(uid)) {
                    review.setRevi(null);
                    review.setDeleted(true);
                    jedis.set(rIdKey, JSON.toJSONString(review), SET_PARAMS_THIRTY_MINUTES);
                    reviewDao.deleteReviewByRid(rid);
                    res = true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    public boolean updateApprove(Long rid, Long uid, Boolean approve) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String rIdKey = getKey(rid, jedis);
            if (rIdKey != null) {
                if (Boolean.TRUE.equals(approve)) jedis.sadd(rIdKey + SUFFIX_APPROVES, uid.toString());
                else jedis.srem(rIdKey + SUFFIX_APPROVES, uid.toString());
                res = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    public ReviewInfo getReviewInfo(Long rid, Long uid) {
        ReviewInfo reviewInfo = new ReviewInfo();
        try (Jedis jedis = getJedis()) {
            String rIdKey = getKey(rid, jedis);
            if (rIdKey != null) {
                Review review = getReview(rIdKey, jedis);
                String rIdAKey = rIdKey + SUFFIX_APPROVES;
                reviewInfo.setApproveCount(jedis.scard(rIdAKey));
                if (uid != null) {
                    reviewInfo.setApproved(jedis.sismember(rIdAKey, uid.toString()));
                }
                reviewInfo.setReview(review);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return reviewInfo;
    }

    private Review getReview(String rIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(rIdKey), Review.class);
    }

    @Override
    public String getKey(Long rid, Jedis jedis) {
        String rIdKey = PREFIX_REVIEW + rid.toString();
        if (jedis.expire(rIdKey, ONE_MINUTE) == 0L) {
            Review review = reviewDao.selectReviewByRid(rid);
            jedis.set(rIdKey, review != null ? JSON.toJSONString(review) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(rIdKey) == 0L ? null : rIdKey;
    }

}
