package com.gzu.queswer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Activity implements Serializable {
    private Long userId;
    private Long gmtCreate;
    private Short act;
    private Long id;
}
