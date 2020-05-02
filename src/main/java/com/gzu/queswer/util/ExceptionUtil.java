package com.gzu.queswer.util;

import com.gzu.queswer.common.UserException;

/**
 * @author 蒋竟成
 * @date 2020/4/29
 */
public class ExceptionUtil {
    public static final UserException NOT_LOGIN=new UserException(10001,"不被允许的未登录操作");
    public static final UserException NOT_AUTHORITY=new UserException(10002,"不被允许的当前权限操作");
}
