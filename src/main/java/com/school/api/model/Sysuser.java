package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Sysuser implements Serializable {

    /**
     * 划分标识
     */
    public static final String SEPARATOR = "#";
    private static final long serialVersionUID = 1L;
    private String id;
    @JSONField(name = "platform_code")
    private String platformCode;
    @JSONField(name = "platform_name")
    private String platformName;
    @JSONField(name = "school_id")
    private Long schoolId;
    @JSONField(name = "school_name")
    private String schoolName;
    @JSONField(name = "login_name")
    private String loginName;
    private String password;
    private String salt;
    private String name;
    @JSONField(name = "img_url")
    private String imgUrl;
    private Short sex;
    @JSONField(name = "type_code")
    private String typeCode;
    @JSONField(name = "test_user")
    private boolean testUser;
    @JSONField(name = "last_login_ip")
    private String lastLoginIp;
    @JSONField(name = "last_login_time")
    private Date lastLoginTime;
    @JSONField(name = "create_time")
    private Date createTime;
    @JSONField(name = "update_time")
    private Date updateTime;
    private Long sort;
    private Short status;
    private boolean del;

}
