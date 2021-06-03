package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysMajor implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "major_code")
    private String majorcode;
    @JSONField(name = "major_name")
    private String majorname;
    private int stat;

}