package com.gzu.queswer.util;

import com.gzu.queswer.common.UserException;

public class ExceptionUtil {
    public static final UserException NOT_LOGIN = new UserException(10001, "不被允许的未登录操作");
    public static final UserException NOT_AUTHORITY = new UserException(10002, "不被允许的当前权限操作");
}
