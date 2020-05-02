package com.gzu.queswer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Message implements Serializable {
    private Long srcId;
    private Long dstId;
    private Long gmtCreate;
    private String msg;
    private Boolean unread;
}
