package com.gzu.queswer.model.vo;

import com.gzu.queswer.model.Review;
import com.gzu.queswer.model.UserApi;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewInfo extends UserApi {
    private Review review;
    private Boolean questioned;
    private Boolean answered;
    private Long approveCount;
    private Boolean approved;
    private Boolean anonymous;
}
