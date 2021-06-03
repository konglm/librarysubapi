package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysMater implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "mater_code")
    private String matercode;
    @JSONField(name = "mater_name")
    private String matername;
    private int stat;

}
