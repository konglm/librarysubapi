package com.school.api.model;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class Sub implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "sub_code", ordinal = 1)
    private String subCode;
    @JSONField(name = "sub_name", ordinal = 2)
    private String subName;
    private List<Sub> subs;

}