package com.gzu.queswer.service;

import com.gzu.queswer.dao.AnswerDaoImpl;
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
    private AnswerDaoImpl answerDaoImpl;
    public Long insertAnswer(Answer answer) {
        answerDaoImpl.insertAnswer(answer);
        return answer.getAid();
    }

    public Integer deleteAnswer(Long aid, Long uid) {
        return answerDaoImpl.deleteAnswer(aid, uid);
    }

    public Integer updateAnswer(Answer answer) {
        return answerDaoImpl.updateAnswer(answer);
    }

    public Integer insertAttitude(Attitude attitude) {
        return answerDaoImpl.insertAttitude(attitude);
    }

    public Integer deleteAttitude(Long aid, Long uid) {
        return answerDaoImpl.deleteAttitude(aid, uid);
    }
@Autowired
    private UserInfoDao userInfoDao;
    public Map getAttitude(Long aid, Long uid) {
        Map map = null;
        if (aid == null) return map;
        map = new HashMap();
        map.put("attitudes", answerDaoImpl.selectAttitudesByAid(aid));
        if (uid == null) return map;
        map.put("attituded", answerDaoImpl.selectAttitudeByUid(aid, uid));
        return map;
    }

    public List getAnswerList(Long qid) {
        return answerDaoImpl.selectAnswersByQid(qid);
    }

    public Answer selectAnswerByUid(Long qid, Long uid) {
        return answerDaoImpl.selectAnswerByUid(qid, uid);
    }
}
