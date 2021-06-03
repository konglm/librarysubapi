package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class SchApp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用系统名字
     */
    private String name;
    /**
     * 应用系统图标
     */
    @JSONField(name = "img_url")
    private String imgUrl;
    /**
     * 权限
     */
    private String access;

}
