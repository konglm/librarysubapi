package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysGrade implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "sys_grd_code")
    private String sysgrdcode;
    @JSONField(name = "sys_grd_name")
    private String sysgrdname;
    private int stat;

}
