package com.gzu.queswer.controller;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.*;
import com.gzu.queswer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @RequestMapping("/addAnswer")
    public Long addAnswer(@RequestBody Answer answer) {
        return answerService.insertAnswer(answer);
    }

    @RequestMapping("/deleteAnswer")
    public Integer deleteAnswer(Long aid, Long uid) {
        return answerService.deleteAnswer(aid, uid);
    }

    @RequestMapping("/addAttitude")
    public Integer addAttitude(Long aid, Long uid, Boolean attitude) {
        return answerService.insetAttitude(aid, uid, attitude);
    }

    @RequestMapping("/deleteAttitude")
    public Integer deleteAttitude(Long aid, Long uid) {
        return answerService.deleteAttitude(aid, uid);
    }


    @RequestMapping("getAnswerList")
    public List getAnswerList(Long qid,Long uid) {
        List list = answerService.getAnswerList(qid);
        userService.setUserInfo(list,uid);
        return list;
    }

    @RequestMapping("getAttitude")
    public Map getAttitude(Long aid, Long uid) {
        return answerService.getAttitude(aid, uid);
    }

    @RequestMapping("addReview")
    public Long addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @RequestMapping("deleteReviewSuper")
    public Integer deleteReviewSuper(Long rid) {
        return reviewService.deleteReviewSuper(rid);
    }

    @RequestMapping("deleteReview")
    public Integer deleteReview(Long rid, Long uid) {
        return reviewService.deleteReview(rid, uid);
    }

    @RequestMapping("getReviewList")
    public List getReviewList(Long aid,Long uid) {
        List<UserInfoApi> reviews = reviewService.getReviewList(aid);
        //通过aid获取回答者和qid
        Map answerer=reviewService.selectAnswererByAid(aid);
        Long qid= (Long) answerer.get("qid");
        //通过qid获取提问者
        Map questioner=reviewService.selectQuestionerByQid(qid);
        Long answerer_uid=(Long) answerer.get("uid");
        Long questioner_uid=(Long) questioner.get("uid");
        Boolean answerer_anonymous=(Boolean)answerer.get("anonymous") ;
        Boolean questioner_anonymous=(Boolean)questioner.get("anonymous") ;
        for(UserInfoApi userInfoApi:reviews){
            Review review= (Review) userInfoApi;
            review.setAnonymous(false);
            if(questioner_uid==review.getUid()){
                review.setQuestioner(true);
                review.setAnonymous(questioner_anonymous);
            }
            if(answerer_uid==review.getUid()){
                review.setAnonymous(answerer_anonymous);
                review.setAnswerer(true);
                review.setQuestioner(false);
            }
        }
        userService.setUserInfo(reviews,uid);
        return reviews;
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
