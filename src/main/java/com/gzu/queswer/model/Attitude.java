package com.gzu.queswer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Attitude implements Serializable {
    private Long answerId;
    private Long userId;
    private Boolean atti;
    private Long gmtCreate;
}
