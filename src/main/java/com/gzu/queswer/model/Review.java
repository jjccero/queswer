package com.gzu.queswer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Review implements Serializable {
    private Long reviewId;
    private Long replyId;
    private Long gmtCreate;
    private Long answerId;
    private String revi;
    private Boolean deleted;
    private Long userId;
}
