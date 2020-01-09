package com.gzu.queswer.controller;

import com.gzu.queswer.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {


    @Autowired
    private IUserService userService;

    @RequestMapping("/")
    public String getPublicKey() {
        //return SecurityUtil.getPublicKey();
        return "你好";
    }

    @RequestMapping("/login")
    public int login(String username,String password){
        return userService.login(username,password);
    }

    @RequestMapping("/signup")
    public int signup(String username,String password){
        return userService.signup(username,password);
    }
}
