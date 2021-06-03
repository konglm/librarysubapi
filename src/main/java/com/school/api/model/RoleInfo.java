package com.school.api.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long roleId;
    private Long gradeId;
    private String gradeCode;
    private String gradeName;
    private Long clsId;
    private String clsCode;
    private String clsName;
    private String subCode;
    private String subName;

    public RoleInfo(Long roleId, Long gradeId, String gradeCode, String gradeName, Long clsId, String clsCode, String clsName, String subCode, String subName) {
        this.roleId = roleId;
        this.gradeId = gradeId;
        this.gradeCode = gradeCode;
        this.gradeName = gradeName;
        this.clsId = clsId;
        this.clsCode = clsCode;
        this.clsName = clsName;
        this.subCode = subCode;
        this.subName = subName;
    }

}
