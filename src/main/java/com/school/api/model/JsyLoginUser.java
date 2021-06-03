package com.school.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JsyLoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean sup; // 是否为超级管理员
    private String ulgt; //上次登录时间
    private String platformCode; // 平台代码
    private String platformName; // 平台名称
    private Long schid; // 学校id
    private String schname; // 学校名称
    private Long utid;
    private String uid;
    private String utname;
    private String imgurl;
    private Short sex;
    private String utp;

    /**
     * 入学月份
     */
    private Integer schopenm;
    private String areano;
    private List<Dpt> dpts;
    private List<Grd> grds;
    private List<Cls> clss;
    private List<SysSub> subs;
    private String urolestr;
    private String urolestrext;
    private String utoken;
    /**
     * 校讯通数据操作者：0支撑系统,1学校
     */
    private Short appxxtdouser;
    /**
     * 校讯通服务状态：0关闭,1正常
     */
    private Short appxxtservstat;
    /**
     * 子系统编辑状态：0不允许编辑、增加和删除，1开启
     */
    private Short appeditstat;
    /**
     * 是否为学校管理员：0非，1是
     */
    private Short isadmin;
    /**
     * 资源平台权限串：对应权限位0无权限，1拥有权限
     */
    private String urolestrsoure;
    /**
     * 校讯通权限串：对应权限位0无权限，1拥有权限
     */
    private String urolestrxxt;
}
