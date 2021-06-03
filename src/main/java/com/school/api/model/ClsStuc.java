package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class ClsStuc implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "cls_code", ordinal = 1)
    private String clsCode;
    @JSONField(ordinal = 2)
    private Integer stuc;

}
