package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Acl implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long pid;
    @JSONField(name = "platform_code")
    private String platformCode;
    private String name;
    private String access;
    private String url;
    private String icon;
    private boolean idlogin;
    private boolean app;
    private boolean sch;
    private Long sort;
    private Short status;
    private boolean del;
    private List<Acl> children;

}
