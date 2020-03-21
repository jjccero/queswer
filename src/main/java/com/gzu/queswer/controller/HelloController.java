package com.gzu.queswer.controller;

import com.alibaba.fastjson.JSONObject;
import com.gzu.queswer.model.Topic;
import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.info.UserInfo;
import com.gzu.queswer.service.TopicService;
import com.gzu.queswer.service.UserService;
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

//    @RequestMapping("/img/{img}")
//    public void getImg(@PathVariable("img")String imgPath, HttpServletResponse response){
//        System.out.println(imgPath);
//    }

//    @RequestMapping("/")
//    public String getPublicKey() {
//        return SecurityUtil.getPublicKey();
//    }

    @RequestMapping(value = "/deleteCache")
    public int deleteCache() {
        return 1;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public User login(@RequestBody JSONObject loginForm) {
        String username = loginForm.getString("username");
        String password = loginForm.getString("password");
        return userService.login(username, password);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public Long signup(@RequestBody UserLogin userLogin) {
        userLogin.setNormalUser();
        return userService.insertUser(userLogin);
    }

    @RequestMapping("/signupSuper")
    public Long signupSuper(@RequestBody UserLogin userLogin) {
        userLogin.setSuperUser();
        return userService.insertUser(userLogin);
    }

    @RequestMapping("/updateUser")
    public Integer updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @RequestMapping("/getFansList")
    public List getFansList(Long support_uid) {
        return userService.selectSupportersByUid(support_uid);
    }

    @RequestMapping(value = "selectUserInfo")
    public UserInfo selectUserInfo(Long people_uid,Long uid){
        return userService.getUserInfo(people_uid);
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
