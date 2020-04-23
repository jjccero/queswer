package com.gzu.queswer.model.info;

import com.gzu.queswer.model.Activity;
import com.gzu.queswer.model.UserApi;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActivityInfo extends UserApi {
    private Activity activity;
    private Object info;
}
