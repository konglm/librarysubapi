package com.school.api.model;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

import java.io.Serializable;

@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class Grd implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "per_code", ordinal = 1)
    private String perCode; // 学段代码
    @JSONField(name = "grd_code", ordinal = 2)
    private String grdCode; // 年级id
    @JSONField(name = "grd_name", ordinal = 3)
    private String grdName; // 年级名称
    @JSONField(name = "sys_grd_code", ordinal = 4)
    private String sysGrdCode; // 年级代码
    @JSONField(name = "grd_year", ordinal = 5)
    private String grdYear; // 入学年份
    @JSONField(name = "is_art", ordinal = 6)
    private Integer isArt; // 是否分科
    @JSONField(name = "is_finish", ordinal = 7)
    private Integer isFinish; // 是否毕业

}