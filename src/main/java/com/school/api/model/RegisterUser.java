package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterUser implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 学校id
     */
    @JSONField(name = "sch_code", ordinal = 1)
    private String schCode;
    /**
     * 学校名称
     */
    @JSONField(name = "sch_name", ordinal = 2)
    private String schName;
    /**
     * 年级名称
     */
    @JSONField(name = "grd_name", ordinal = 3)
    private String grdName;
    /**
     * 班级名称
     */
    @JSONField(name = "cls_name", ordinal = 4)
    private String clsName;
    /**
     * 用户类型
     */
    @JSONField(name = "user_type", ordinal = 4)
    private String userType;
    @JSONField(name = "user_code", ordinal = 5)
    private String userCode;
    @JSONField(name = "user_name", ordinal = 6)
    private String userName;
    /**
     * 电话
     */
    @JSONField(ordinal = 7)
    private String phone;
    /**
     * 登录名
     */
    @JSONField(name = "login_name", ordinal = 8)
    private String loginName;


}
