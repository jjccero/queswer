package com.gzu.queswer.controller;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.*;
import com.gzu.queswer.service.AnswerService;
import com.gzu.queswer.service.ReviewService;
import com.gzu.queswer.service.TopicService;
import com.gzu.queswer.service.UserService;
import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {
    @Autowired
    private UserService userService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private ReviewService reviewService;

//    @RequestMapping("/img/{img}")
//    public void getImg(@PathVariable("img")String imgPath, HttpServletResponse response){
//        System.out.println(imgPath);
//    }

//    @RequestMapping("/")
//    public String getPublicKey() {
//        return SecurityUtil.getPublicKey();
//    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public User login(@RequestBody JSONObject loginForm) {
        String username = loginForm.getString("username");
        String password = loginForm.getString("password");
        return userService.login(username, password);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public Long signup(@RequestBody User user) {
        user.setNormalUser();
        return userService.insertUser(user);
    }

    @RequestMapping("/signupSuper")
    public Long signupSuper(@RequestBody User user) {
        user.setSuperUser();
        return userService.insertUser(user);
    }

    @RequestMapping("/updateUser")
    public Integer updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @RequestMapping("/getFansList")
    public List getFansList(Long support_uid) {
        return userService.selectSupportersByUid(support_uid);
    }

    @RequestMapping(value = "/addAnswer", method = RequestMethod.POST)
    public Long addAnswer(@RequestBody Answer answer) {
        answer.setAnswer_time(DateUtil.getUnixTime());
        return answerService.insertAnswer(answer);
    }

    @RequestMapping("/deleteAnswer")
    public boolean deleteAnswer(Long aid, Long uid) {
        return answerService.deleteAnswer(aid, uid);
    }

    @RequestMapping("/addAttitude")
    public boolean addAttitude(@RequestBody Attitude attitude) {
        return answerService.insertAttitude(attitude);
    }

    @RequestMapping("/deleteAttitude")
    public boolean deleteAttitude(long aid, long uid) {
        return answerService.deleteAttitude(aid, uid);
    }


    @RequestMapping("getAnswers")
    public List getAnswers(Long qid,Long uid) {
        List list = answerService.getAnswers(qid,uid);
        return list;
    }


    @RequestMapping("addReview")
    public Long addReview(@RequestBody Review review) {
        review.setReview_time(DateUtil.getUnixTime());
        return reviewService.addReview(review);
    }

    @RequestMapping("deleteReviewSuper")
    public Integer deleteReviewSuper(Long rid) {
        return reviewService.deleteReviewSuper(rid);
    }

    @RequestMapping("deleteReview")
    public boolean deleteReview(Long rid, Long uid) {
        return reviewService.deleteReview(rid, uid);
    }

    @RequestMapping("getReviews")
    public List getReviews(Long aid,Long uid) {
        return reviewService.getReviews(aid,uid);
    }

    @Autowired
    TopicService topicService;

    @RequestMapping("getTopicList")
    public List getTopicList() {
        return topicService.selectTopics();
    }

    @RequestMapping("addTopic")
    public Long addTopic(@RequestBody Topic topic) {
        return topicService.insertTopic(topic);
    }


}
