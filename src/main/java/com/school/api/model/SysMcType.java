package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysMcType implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "mchtp_code")
    private String mchtpcode;
    @JSONField(name = "mchtp_name")
    private String mchtpname;
    private int stat;

}
