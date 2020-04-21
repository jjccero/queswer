package com.gzu.queswer.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Message {
    private Long srcId;
    private Long dstId;
    private Long gmtCreate;
    private String msg;
    private Boolean unread;
}
