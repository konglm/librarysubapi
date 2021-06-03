package com.school.library.constants;

/**
 * @Description redis常量类
 * @Author jsy
 * @Date 2020/3/16
 * @Version V1.0
 **/

public class RedisConstants {

    /**
     * 插入学校借出设置记录时的key前缀
     */
    public static final String BORROW_SETTING_KEY_PREFIX = "borrow_setting_";

    /**
     * 插入系统默认借出设置记录时的key
     */
    public static final String SYS_BORROW_SETTING_KEY = "sys_borrow_setting";

    /**
     * 插入图书分类目录设置记录时的key前缀
     */
    public static final String CATALOG_SETTING_KEY_PREFIX = "catalog_setting_";

    /**
     * 插入系统默认图书分类目录记录时的key
     */
    public static final String SYS_CATALOG_SETTING_KEY = "sys_catalog_setting";

    /**
     * 对象默认存活时间-一天
     */
    public static final Long TIME_TO_LIVE_SECONDS = 86400L;

    /**
     * 图书入库事件key前缀
     */
    public static final String BOOK_STORAGE_KEY_PREFIX = "storage_";

    /**
     * 每天新建图书入库事件次数的key前缀
     */
    public static final String BOOK_STORAGE_COUNT_KEY_PREFIX = "storage_count_";

    /**
     * 索书号计数器key前缀
     */
    public static final String CHECK_NO_COUNT_KEY_PREFIX = "check_no_";

    /**
     * 同步学生人事数据key前缀
     */
    public static final String SYNCHRONIZE_STU_KEY_PREFIX = "sync_stu_";

    /**
     * 同步教师人事数据key前缀
     */
    public static final String SYNCHRONIZE_TEACHER_KEY_PREFIX = "sync_teacher_";

    /**
     * 图书盘点key前缀
     */
    public static final String BOOK_INVENTORY_KEY_PREFIX = "inventory_";

    /**
     * 生成bar_code序号的key前缀
     */
    public static final String BAR_CODE_SEQ_KEY_PREFIX = "bar_code_seq_";

}
