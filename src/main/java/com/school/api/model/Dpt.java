package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class Dpt implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "dpt_code", ordinal = 1)
    private String dptCode;
    @JSONField(name = "dpt_name", ordinal = 2)
    private String dptName;
    @JSONField(name = "pcode", ordinal = 3)
    private String pcode;
    @JSONField(name = "sort", ordinal = 4)
    private Long sort;

}