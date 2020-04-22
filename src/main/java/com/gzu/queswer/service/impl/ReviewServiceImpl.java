package com.gzu.queswer.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.dao.ReviewDao;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Question;
import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.info.ReviewInfo;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.service.AnswerService;
import com.gzu.queswer.service.QuestionService;
import com.gzu.queswer.service.ReviewService;
import com.gzu.queswer.service.UserService;
import com.gzu.queswer.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ReviewServiceImpl extends RedisService implements ReviewService {
    @Autowired
    ReviewDao reviewDao;
    @Autowired
    UserService userService;
    @Autowired
    AnswerService answerService;
    @Autowired
    QuestionService questionService;

    @Override
    public Long saveReview(Review review) {
        review.setReviewId(null);
        review.setGmtCreate(DateUtil.getUnixTime());
        reviewDao.insertReview(review);
        Long reviewId = review.getReviewId();
        if (reviewId != null) {
            try (Jedis jedis = getJedis()) {
                jedis.zadd(PREFIX_ANSWER + review.getAnswerId().toString() + SUFFIX_REVIEWS, 0.0, reviewId.toString());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return reviewId;
    }

    @Override
    public boolean deleteReview(Long reviewId, Long userId) {
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String reviewIdKey = getKey(reviewId, jedis);
            if (reviewIdKey != null) {
                Review review = getReview(reviewIdKey, jedis);
                if (review.getUserId().equals(userId)) {
                    review.setRevi(null);
                    review.setDeleted(true);
                    jedis.set(reviewIdKey, JSON.toJSONString(review), SET_PARAMS_ONE_MINUTE);
                    reviewDao.deleteReview(reviewId);
                    res = true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    @Override
    public List<ReviewInfo> queryReviews(Long answerId, Long userId) {
        List<ReviewInfo> reviewInfos = null;
        Answer answer = answerService.getAnswer(answerId);
        Question question = questionService.getQuestion(answer.getQuestionId());
        Long answerUId = answer.getUserId();
        Long questionUId = question.getUserId();
        Boolean answerAnonymous = answer.getAnonymous();
        Boolean questionAnonymous = question.getAnonymous();
        try (Jedis jedis = getJedis()) {
            String aIdRKey = PREFIX_ANSWER + answerId + SUFFIX_REVIEWS;
            Set<String> reviewIdStrings = jedis.zrange(aIdRKey, 0L, -1L);
            reviewInfos = new ArrayList<>(reviewIdStrings.size());
            for (String reviewIdString : reviewIdStrings) {
                Long reviewId = Long.parseLong(reviewIdString);
                ReviewInfo reviewInfo = getReviewInfo(reviewId, userId);
                Review review = reviewInfo.getReview();
                Long reviewerId = review.getUserId();
                reviewInfo.setAnonymous(false);
                //判断提问者和评论者是不是同一人
                if (questionUId.equals(reviewerId)) {
                    reviewInfo.setQuestioned(true);
                    reviewInfo.setAnonymous(questionAnonymous);
                } else reviewInfo.setQuestioned(false);
                //判断提问者和回答者是不是同一人
                if (answerUId.equals(reviewerId)) {
                    //就算提问者也是回答者，也当作回答者看待
                    reviewInfo.setQuestioned(false);
                    reviewInfo.setAnswered(true);
                    reviewInfo.setAnonymous(answerAnonymous);
                } else reviewInfo.setAnswered(false);
                setUserInfo(reviewInfo, userId);
                reviewInfos.add(reviewInfo);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return reviewInfos;
    }

    @Override
    public boolean updateApprove(Long reviewId, Long userId, Boolean approve) {
        if (reviewId == null || userId == null || approve == null) return false;
        boolean res = false;
        try (Jedis jedis = getJedis()) {
            String reviewIdKey = getKey(reviewId, jedis);
            if (reviewIdKey != null) {
                if (Boolean.TRUE.equals(approve)) jedis.sadd(reviewIdKey + SUFFIX_APPROVERS, userId.toString());
                else jedis.srem(reviewIdKey + SUFFIX_APPROVERS, userId.toString());
                res = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    private ReviewInfo getReviewInfo(Long reviewId, Long uId) {
        ReviewInfo reviewInfo = new ReviewInfo();
        try (Jedis jedis = getJedis()) {
            String reviewIdKey = getKey(reviewId, jedis);
            if (reviewIdKey != null) {
                Review review = getReview(reviewIdKey, jedis);
                String reviewIdAKey = reviewIdKey + SUFFIX_APPROVERS;
                //查询赞同数量
                reviewInfo.setApproveCount(jedis.scard(reviewIdAKey));
                //查询是否已赞同
                if (uId != null) reviewInfo.setApproved(jedis.sismember(reviewIdAKey, uId.toString()));
                reviewInfo.setReview(review);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return reviewInfo;
    }

    private Review getReview(String reviewIdKey, Jedis jedis) {
        return JSON.parseObject(jedis.get(reviewIdKey), Review.class);
    }

    private String getKey(Long reviewId, Jedis jedis) {
        String reviewIdKey = PREFIX_REVIEW + reviewId.toString();
        if (jedis.expire(reviewIdKey, ONE_MINUTE) == 0L) {
            Review review = reviewDao.selectReview(reviewId);
            jedis.set(reviewIdKey, review != null ? JSON.toJSONString(review) : "", SET_PARAMS_ONE_MINUTE);
        }
        return jedis.strlen(reviewIdKey) == 0L ? null : reviewIdKey;
    }

    private void setUserInfo(ReviewInfo reviewInfo, Long uid) {
        Review review = reviewInfo.getReview();
        UserInfo userInfo;
        Boolean anonymous = reviewInfo.getAnonymous();
        if (Boolean.TRUE.equals(anonymous) && !review.getUserId().equals(uid)) {
            userInfo = UserInfo.defaultUserInfo;
            review.setUserId(null);
        } else {
            userInfo = userService.getUserInfo(review.getUserId(), uid);
            userInfo.setAnonymous(anonymous);
        }
        reviewInfo.setUserInfo(userInfo);
    }

}
