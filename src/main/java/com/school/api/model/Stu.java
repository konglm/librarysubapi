package com.school.api.model;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

import java.io.Serializable;

@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class Stu implements Serializable {

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
    @JSONField(name = "stu_code", ordinal = 6)
    private String stuCode;
    @JSONField(name = "stu_name", ordinal = 7)
    private String stuName;
    /**
     * 学号（考号）
     */
    @JSONField(ordinal = 8)
    private String sno;
    /**
     * 性别
     */
    @JSONField(ordinal = 9)
    private Short sex;
    /**
     * 图像
     */
    @JSONField(name = "img_url", ordinal = 10)
    private String imgUrl;

}
