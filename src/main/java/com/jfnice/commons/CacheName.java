package com.jfnice.commons;

import com.jfinal.core.JFinal;

/**
 * 缓存名
 */
public class CacheName {

    private static final String WEB_NAME = ":" + JFinal.me().getServletContext().getContextPath();

    public static final String USER_PERMISSION = "userPermission"; // 用户权限
    public static final String USER_DATA_RANGE = "userDataRange"; // 数据范围

    /**
     * 游客登录时保存的数据
     */
    public static final String TOURIST_USER_CACHE = "tourist_user" + WEB_NAME;
    public static final String TOURIST_TOKEN_CACHE = "tourist_token" + WEB_NAME;

    public static final String ERROR_URL = "JnErrorUrl";
    public static final String LOGIN_URL = "JnLoginUrl";

    public static final String ASSET = "JnAsset" + WEB_NAME;
    public static final String VERIFY_CODE = "JnVerifyCode" + WEB_NAME;
    public static final String REFRESH_TOKEN = "JnRefreshToken" + WEB_NAME;
    public static final String DICT = "JnDict" + WEB_NAME;
    public static final String ALL_ID_MAP = "JnAllIdMap" + WEB_NAME;
    public static final String NORMAL_ID_MAP = "JnNormalIdMap" + WEB_NAME;
    public static final String SETTING = "JnSetting" + WEB_NAME;
    public static final String DEFAULT_KEY_TITLE = "JnDefaultKeyTitle" + WEB_NAME;
    public static final String DEFAULT_FIELD_TITLE = "JnDefaultFieldTitle" + WEB_NAME;

    //子系统保存自己变量时的默认缓存名
    public static final String DEFAULT_SUB_NAME = "Default" + WEB_NAME;

    // 学年
    public static final String SCH_YEAR_MAP = "SchBase:YearMap";
    // 学校应用系统个性化信息 -->> 学校系统名称与图标
    public static final String SCH_APP_MAP = "SchBase:AppMap";
    // 学校部门 -->> 组织机构
    public static final String SCH_DPT_MAP = "SchBase:DptMap";
    // 学校学段 -->> 学段与年级选择
    public static final String SCH_PER_MAP = "SchBase:PerMap";
    // 学校年级 -->> 学段与年级选择、升级、分科选择、班级设置
    public static final String SCH_GRD_MAP = "SchBase:GrdMap";
    // 学校班级 -->> 学段与年级选择、开设科目、分科选择、班级设置、别名设置、班级任教、升级
    public static final String SCH_CLS_MAP = "SchBase:ClsMap";
    // 学校学生 -->> 学段与年级选择、班级设置、学生管理
    public static final String SCH_STU_MAP = "SchBase:StuMap";
    // 学校科目 -->> 开设科目
    public static final String SCH_SUB_MAP = "SchBase:SubMap";
    // 学校分科 -->> 学段与年级选择、开设科目、分科选择
    public static final String SCH_ART_MAP = "SchBase:ArtMap";
    // 用户科目组长 -->> 开设科目、学科组长管理
    public static final String SCH_USER_SUB_MAP = "SchBase:UserSubMap";
    // 用户年级组长 -->> 学段与年级选择、年级组长管理
    public static final String SCH_USER_GRD_MAP = "SchBase:UserGrdMap";
    // 用户任课老师 -->> 学段与年级选择、开设科目、班级设置、班级任教
    public static final String SCH_USER_CLS_MAP = "SchBase:UserClsMap";
    // 用户部门 -->> 组织架构、教师及账号权限信息
    public static final String SCH_USER_ORG_MAP = "SchBase:UserOrgMap";


    // 3.4: 系统学期
    public static final String JSY_SYS_TERM = "JsyBase:TermMap";
    // 3.5: 系统学段
    public static final String JSY_SYS_PER = "JsyBase:PerMap";
    // 3.6: 系统分册
    public static final String JSY_SYS_FASC = "JsyBase:FascMap";
    // 3.7: 系统教版
    public static final String JSY_SYS_MATER = "JsyBase:MaterMap";
    // 3.8: 系统科目
    public static final String JSY_SYS_SUB = "JsyBase:SubMap";
    // 3.11: 系统年级
    public static final String JSY_SYS_GRADE = "JsyBase:GradeMap";
    // 3.12: 系统学科
    public static final String JSY_SYS_ART = "JsyBase:ArtMap";
    // 3.13: 系统学院
    public static final String JSY_SYS_COLL = "JsyBase:CollMap";
    // 3.14: 系统专业
    public static final String JSY_SYS_MAJOR = "JsyBase:MajorMap";
    // 3.15: 系统设备类型
    public static final String JSY_SYS_MCTYPE = "JsyBase:McTypeMap";
    // 3.16: 栏目对应表
    public static final String JSY_SYS_ITEM = "JsyBase:ItemMap";

}
