package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SysSub implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "sub_code")
    private String subcode;
    @JSONField(name = "sub_name")
    private String subname;
    private List<SysSub> subs;

}