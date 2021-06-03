package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysArea implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "area_code")
    private String areaCode;
    @JSONField(name = "area_name")
    private String areaName;
    private int stat;

}
