package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class ClsTec implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "grd_code", ordinal = 1)
    private String grdCode;
    @JSONField(name = "sys_grd_code", ordinal = 2)
    private String sysGrdCode;
    @JSONField(name = "grd_name", ordinal = 3)
    private String grdName;
    @JSONField(name = "cls_code", ordinal = 4)
    private String clsCode;
    @JSONField(name = "cls_name", ordinal = 5)
    private String clsName;
    @JSONField(name = "sub_code", ordinal = 6)
    private String subCode;
    @JSONField(name = "sub_name", ordinal = 7)
    private String subName;
    @JSONField(name = "user_code", ordinal = 8)
    private String userCode;
    @JSONField(name = "user_name", ordinal = 9)
    private String userName;
    @JSONField(name = "is_master", ordinal = 10)
    private Integer isMaster;
    @JSONField(name = "phone", ordinal = 11)
    private String phone;

}
