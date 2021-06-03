package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class Year implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "year_code")
    private String yearCode;
    @JSONField(name = "year_name")
    private String yearName;

}
