package com.school.api.model;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

import java.io.Serializable;

@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class Cls implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 年级id
     */
    @JSONField(name = "grd_code", ordinal = 1)
    private String grdCode;
    /**
     * 年级代码
     */
    @JSONField(name = "sys_grd_code", ordinal = 2)
    private String sysGrdCode;
    /**
     * 年级名称
     */
    @JSONField(name = "grd_name", ordinal = 3)
    private String grdName;
    /**
     * 科目代码
     */
    @JSONField(name = "sub_code", ordinal = 4)
    private String subCode;
    /**
     * 班级id
     */
    @JSONField(name = "cls_code", ordinal = 5)
    private String clsCode;
    /**
     * 班级代码
     */
    @JSONField(name = "cls_num", ordinal = 6)
    private String clsNum;
    /**
     * 班级名称
     */
    @JSONField(name = "cls_name", ordinal = 7)
    private String clsName;
    /**
     * 是否为班主任
     */
    @JSONField(name = "is_master", ordinal = 8)
    private Integer isMaster;
    /**
     * 班主任id
     */
    @JSONField(name = "master_code", ordinal = 8)
    private String masterCode;
    /**
     * 班主任名
     */
    @JSONField(name = "master_name", ordinal = 9)
    private String masterName;
    /**
     * 班主任电话
     */
    @JSONField(name = "master_phone", ordinal = 10)
    private String masterPhone;
    /**
     * 班级学生人数
     */
    private Short stuc;
    /**
     * 分科类型
     */
    @JSONField(name = "art_code", ordinal = 11)
    private String artCode;
    /**
     * 是否毕业
     */
    @JSONField(name = "is_finish", ordinal = 12)
    private Integer isFinish;

}