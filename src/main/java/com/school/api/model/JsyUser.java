package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class JsyUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学校id
     */
    @JSONField(name = "sch_code")
    private String schCode;
    /**
     * 用户id
     */
    @JSONField(name = "user_code")
    private String userCode;
    /**
     * 用户姓名
     */
    @JSONField(name = "user_name")
    private String userName;
    /**
     * 头像
     */
    @JSONField(name = "img_url")
    private String imgUrl;
    /**
     * 性别
     */
    private Short sex;
    /**
     * 电话
     */
    private String phone;
    /**
     * 部门id
     */
    @JSONField(name = "dpt_code")
    private String dptCode;
    @JSONField(name = "dpt_name")
    private String dptName;
    /**
     * 是否有账号 1有账号 0无账号
     */
    @JSONField(name = "uid_stat")
    private Integer uidStat;

    public JsyUser() {
    }

    public JsyUser(String schCode, String userCode) {
        this.schCode = schCode;
        this.userCode = userCode;
    }

}
