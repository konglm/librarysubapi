package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accessToken;
    @JSONField(name = "platform_code")
    private String platformCode;
    @JSONField(name = "platform_name")
    private String platformName;
    @JSONField(name = "unit_code")
    private String schoolCode;
    @JSONField(name = "unit_name")
    private String schoolName;
    @JSONField(name = "app_code")
    private String appCode;
    @JSONField(name = "user_code")
    private String userCode;
    @JSONField(name = "user_name")
    private String userName;
    @JSONField(name = "login_name")
    private String loginName;
    @JSONField(name = "img_url")
    private String imgUrl;
    @JSONField(name = "type_code")
    private String typeCode;
    @JSONField(name = "sex")
    private Short sex;
    @JSONField(name = "index_code")
    private String indexCode;

}
