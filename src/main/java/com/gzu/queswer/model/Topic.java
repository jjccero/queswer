package com.gzu.queswer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Topic implements Serializable {
    private Long topicId;
    private String topicName;
    private String topicIntro;
}
