package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class School implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "unit_code")
    private String code;
    @JSONField(name = "unit_name")
    private String name;
    private Short status;
    private boolean del;

}
