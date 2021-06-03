package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SysPer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学段代码
     */
    @JSONField(name = "per_code")
    private String percode;
    /**
     * 学段名称
     */
    @JSONField(name = "per_name")
    private String pername;
    /**
     * 学段时长
     */
    @JSONField(name = "per_year")
    private String peryear;
    /**
     * 学段包含年级信息
     */
    @JSONField(name = "grd_list")
    private List<SysGrade> grdList;
    private int stat;

}
