package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysColl implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "coll_code")
    private String collcode;
    @JSONField(name = "coll_name")
    private String collname;
    private int stat;

}