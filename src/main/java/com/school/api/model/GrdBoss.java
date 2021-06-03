package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class GrdBoss implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "grd_code", ordinal = 1)
    private String grdCode; // 年级id
    @JSONField(name = "grd_name", ordinal = 2)
    private String grdName; // 年级名称
    @JSONField(name = "sys_grd_code", ordinal = 3)
    private String sysGrdCode; // 年级代码
    @JSONField(name = "user_code", ordinal = 4)
    private String userCode;
    @JSONField(name = "user_name", ordinal = 5)
    private String userName;

}