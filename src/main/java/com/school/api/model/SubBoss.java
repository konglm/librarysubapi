package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class SubBoss implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "sub_code", ordinal = 1)
    private String subCode;
    @JSONField(name = "user_code", ordinal = 4)
    private String userCode;
    @JSONField(name = "user_name", ordinal = 5)
    private String userName;

}
