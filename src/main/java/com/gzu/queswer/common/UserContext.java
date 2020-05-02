package com.gzu.queswer.common;

import com.gzu.queswer.model.User;
import com.gzu.queswer.util.ExceptionUtil;
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

    public User getUser(boolean throwException) throws UserException {
        User user = userThreadLocal.get();
        if (user == null && throwException) throw ExceptionUtil.NOT_LOGIN;
        return user;
    }

    public void remove() {
        userThreadLocal.remove();
    }

    public Long getUserId(boolean throwException) throws UserException {
        User user = getUser(throwException);
        return user != null ? user.getUserId() : null;
    }
}
