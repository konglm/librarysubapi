package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysFasc implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "fasc_code")
    private String fasccode;
    @JSONField(name = "fasc_name")
    private String fascname;
    private int stat;

}
