package com.gzu.queswer.common;

/**
 * @author 蒋竟成
 * @date 2020/4/28
 */
public class UserException extends Exception {
    private Integer code;

    public UserException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
