package com.gzu.queswer.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TopicActivity {
    private Long userId;
    private Long gmtCreate;
    private String topic;
}
