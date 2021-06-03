package com.school.api.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Par implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long parid;
    /**
     * 姓名
     */
    private String parname;
    /**
     * 学生id
     */
    private Long stuid;
    /**
     * 学生姓名
     */
    private String stuname;
    /**
     * 性别
     */
    private Short sex;
    /**
     * 电话
     */
    private String phone;
    /**
     * 关系
     */
    private String relationship;

}
