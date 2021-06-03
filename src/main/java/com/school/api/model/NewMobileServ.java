package com.school.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class NewMobileServ implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    private String mobile;
    /**
     * 套餐代码
     */
    private String servid;
    /**
     * 套餐名称
     */
    private String cnname;
    /**
     * 开始时间
     */
    private Date rectime;
    /**
     * 结束时间
     */
    private Date endtime;
    /**
     * 服务状态:0停止,1正常
     */
    private Short servstat;

}
