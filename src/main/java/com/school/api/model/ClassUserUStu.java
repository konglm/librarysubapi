package com.school.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 4.10: 学校班级订购学生
 */
@Getter
@Setter
public class ClassUserUStu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学校id
     */
    private Long schid;
    /**
     * 学校名称
     */
    private String schname;
    /**
     * 年级id
     */
    private Long grdid;
    /**
     * 年级代码
     */
    private String grdcode;
    /**
     * 年级名称
     */
    private String grdname;
    /**
     * 班级id
     */
    private Long clsid;
    /**
     * 班级名称
     */
    private String clsname;
    /**
     * 学生id
     */
    private String stuid;
    /**
     * 学生姓名
     */
    private String stuname;
    /**
     * 家长id
     */
    private String utid;
    /**
     * 家长姓名
     */
    private String utname;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 套餐代码
     */
    private String servid;

}
