package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysArt implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "arts_code")
    private String artscode;
    @JSONField(name = "arts_name")
    private String artsname;
    private int stat;

}