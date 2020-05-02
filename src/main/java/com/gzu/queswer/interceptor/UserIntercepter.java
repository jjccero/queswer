package com.gzu.queswer.interceptor;

import com.gzu.queswer.common.UserContext;
import com.gzu.queswer.model.User;
import com.gzu.queswer.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class UserIntercepter implements HandlerInterceptor {
    @Autowired
    UserContext userContext;
    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token != null) {
            User user = userService.getUserByToken(token);
            if (user != null) {
                userContext.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        userContext.remove();
    }
}
