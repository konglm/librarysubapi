package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    @JSONField(name = "platform_code")
    private String platformCode;
    @JSONField(name = "school_id")
    private Long schoolId;
    @JSONField(name = "acl_id")
    private Long aclId;
    private String name;
    private String description;
    private Short type;
    private boolean sys;
    private boolean sch;
    private Long sort;
    private Short status;
    private boolean del;

}
