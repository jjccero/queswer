package com.gzu.queswer.common;

import com.gzu.queswer.model.User;
import org.springframework.stereotype.Component;

/**
 * @author 蒋竟成
 * @date 2020/4/27
 */
@Component
public class UserContext {
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public void setUser(User user) {
        userThreadLocal.set(user);
    }

    public User getUser() {
        return userThreadLocal.get();
    }

    public void remove() {
        userThreadLocal.remove();
    }
}
