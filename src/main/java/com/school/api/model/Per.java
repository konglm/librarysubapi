package com.school.api.model;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

import java.io.Serializable;

@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class Per implements Serializable {
    private static final long serialVersionUID = 1L;

    @JSONField(name = "per_code")
    private String perCode;
    @JSONField(name = "per_name")
    private String perName;
}
