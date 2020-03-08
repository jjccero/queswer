package com.gzu.queswer.service;

import com.gzu.queswer.dao.AnswerDao;
import com.gzu.queswer.dao.AttitudeDao;
import com.gzu.queswer.dao.UserInfoDao;
import com.gzu.queswer.model.Answer;
import com.gzu.queswer.model.Attitude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnswerService {
    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private AttitudeDao attitudeDao;
    public Long insertAnswer(Answer answer) {
        answerDao.insertAnswer(answer);
        return answer.getAid();
    }

    public Integer deleteAnswer(Long aid, Long uid) {
        return answerDao.deleteAnswer(aid, uid);
    }

    public Integer updateAnswer(Answer answer) {
        return answerDao.updateAnswer(answer);
    }

    public Integer insertAttitude(Attitude attitude) {
        return attitudeDao.insertAttitude(attitude);
    }

    public Integer deleteAttitude(Long aid, Long uid) {
        return attitudeDao.deleteAttitude(aid, uid);
    }
@Autowired
    private UserInfoDao userInfoDao;
    public Map getAttitude(Long aid, Long uid) {
//        redisDao.hello();
        Map map = null;
        if (aid == null) return map;
        map = new HashMap();
        map.put("attitudes", attitudeDao.selectAttitudeByAid(aid));
        if (uid == null) return map;
        map.put("attituded", attitudeDao.selectAttitudeByUid(aid, uid));
        return map;
    }

    public List getAnswerList(Long qid) {
        return answerDao.selectAnswersByQid(qid);
    }

    public Answer selectAnswerByUid(Long qid, Long uid) {
        return answerDao.selectAnswerByUid(qid, uid);
    }
}
