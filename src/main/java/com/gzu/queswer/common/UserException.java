package com.gzu.queswer.common;


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
