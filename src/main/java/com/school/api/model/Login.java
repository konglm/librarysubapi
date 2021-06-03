package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Login {

    @JSONField(name = "id")
    private Long id;
    @JSONField(name = "user_name")
    private String userName;
    @JSONField(name = "user_type")
    private String userType;
    @JSONField(name = "access_token")
    private String accessToken;
    @JSONField(name = "max_idle_time_in_seconds")
    private Integer maxIdleTimeInSeconds;

}
