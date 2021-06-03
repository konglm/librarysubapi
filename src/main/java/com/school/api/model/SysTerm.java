package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysTerm implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "term_code")
    private String termcode;
    @JSONField(name = "term_name")
    private String termname;
    private int stat;

}
