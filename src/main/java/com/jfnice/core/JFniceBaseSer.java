package com.jfnice.core;

import com.jfinal.kit.Okv;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Table;
import com.jfnice.ext.CondPara;

import java.util.List;
import java.util.Map;

interface JFniceBaseSer<M extends Model<M>> {

    /**
     * 获取对应Table类
     *
     * @return Table
     * @author JFnice
     */
    Table getTable();

    /**
     * 获取对应数据库表名
     *
     * @return String
     * @author JFnice
     */
    String getTableName();

    /**
     * 获取主键名
     *
     * @return String
     * @author JFnice
     */
    String getPrimaryKey();

    /**
     * 获取主键类型
     *
     * @return Class
     * @author JFnice
     */
    Class<?> getPrimaryKeyType();

    /**
     * 获取主键的父级字段名
     *
     * @return Class
     * @author JFnice
     */
    String getParentKey();

    /**
     * 获取包含所有字段的Model
     *
     * @param id 主键值
     * @return Model
     * @author JFnice
     */
    <T> M queryById(T id);

    /**
     * 获取指定字段集的Model
     *
     * @param id     主键值
     * @param fields 指定获取的字段集，如："id, name"
     * @return Model
     * @author JFnice
     */
    <T> M queryById(T id, String fields);

    /**
     * 获取指定字段集的Model列表
     *
     * @param ids    主键数组
     * @param fields 指定获取的字段集，如："id, name"
     * @return List
     * @author JFnice
     */
    <T> List<M> queryListByIds(T[] ids, String fields);

    /**
     * 获取指定字段集的Model列表
     *
     * @param ids    主键数组
     * @param fields 指定获取的字段集，如："id, name"
     * @param orders 指定排序的字段
     * @return List
     * @author JFnice
     */
    <T> List<M> queryListByIds(T[] ids, String fields, String orders);

    /**
     * 获取指定字段集的Model列表 [ 只包含字段 status = 1 状态的数据 ]
     *
     * @param fields 指定获取的字段集，如："id, name"
     * @return List
     * @author JFnice
     */
    List<M> queryList(String fields);

    /**
     * 获取指定字段集及排序的Model列表  [ 只包含字段 status = 1 状态的数据 ]
     *
     * @param fields 指定获取的字段集，如："id, name"
     * @param orders 自定义排序字符串，如： "id DESC, name ASC"
     * @return List
     * @author JFnice
     */
    List<M> queryList(String fields, String orders);

    /**
     * 获取指定字段集的Model条件列表
     *
     * @param fields     指定获取的字段集，如："id, name"
     * @param conditions 自定义条件的Okv，如id>100： conditions.set("id > ", 100)
     * @return List
     * @author JFnice
     */
    List<M> queryList(String fields, Okv conditions);

    /**
     * 获取指定字段集及排序的Model条件列表
     *
     * @param fields     指定获取的字段集，如："id, name"
     * @param conditions 自定义条件的Okv，如id>100： conditions.set("id > ", 100)
     * @param orders     自定义排序字符串，如： "id DESC, name ASC"
     * @return List
     * @author JFnice
     */
    List<M> queryList(String fields, Okv conditions, String orders);

    /**
     * 获取Model列表
     *
     * @param condPara 指定condPara参数
     * @return Page
     * @author JFnice
     */
    List<M> queryList(CondPara condPara);

    /**
     * 获取Model分页
     *
     * @param condPara 指定condPara参数
     * @return Page
     * @author JFnice
     */
    Page<M> queryPage(CondPara condPara);

    /**
     * 获取包含所有字段的Model子级列表  [ 只包含字段 status = 1 状态的数据 ]
     *
     * @param id     主键值
     * @param fields 指定获取的字段集，如："id, name"
     * @return List
     * @author JFnice
     */
    <T> List<M> queryChildList(T id, String fields);

    /**
     * 获取指定字段集的Model子级列表
     *
     * @param id              主键值
     * @param fields          指定获取的字段集，如："id, name"
     * @param isShowAllStatus true：显示字段status所有状态的数据；false：只显示字段 status = 1 状态的数据
     * @return List
     * @author JFnice
     */
    <T> List<M> queryChildList(T id, String fields, boolean isShowAllStatus);

    /**
     * 获取指定字段集的Model所有子级树状列表及其自身  [ 只包含字段 status = 1 状态的数据 ]
     *
     * @param id     主键值
     * @param fields 指定获取的字段集，如："id, name"
     * @return List
     * @author JFnice
     */
    <T> List<M> queryAllChildList(T id, String fields);

    /**
     * 获取指定字段集的Model所有子级树状列表及其自身
     *
     * @param id              主键值
     * @param fields          指定获取的字段集，如："id, name"
     * @param isShowAllStatus true：显示字段status所有状态的数据；false：只显示字段 status = 1 状态的数据
     * @return List
     * @author JFnice
     */
    <T> List<M> queryAllChildList(T id, String fields, boolean isShowAllStatus);

    /**
     * 获取Model的所有子级id及其自身的集合  [ 只包含字段 status = 1 状态的数据 ]
     *
     * @param id 主键值
     * @return List
     * @author JFnice
     */
    <T> List<T> queryAllChildIds(T id);

    /**
     * 获取指定字段集的Model所有父级顺序列表（不包含其自身） [ List排序：自顶向下排序 ]
     *
     * @param id     主键值
     * @param fields 指定获取的字段集，如："id, name"
     * @return List
     * @author JFnice
     */
    <T> List<M> queryParentList(T id, String fields);

    /**
     * 获取Model的所有父级id的集合（不包含其自身）
     *
     * @param id 主键值
     * @return List
     * @author JFnice
     */
    <T> List<T> queryParentIds(T id);

    /**
     * 获取最顶层Model [ 即  pid = 0 的Model ]
     *
     * @param id 主键值
     * @return Model
     * @author JFnice
     */
    <T> M queryRootById(T id);

    /**
     * 获取指定字段集的Model所有关系列表及其自身 [ 得到未排序列表 ]
     *
     * @param id                主键值
     * @param fields            指定获取的字段集，如："id, name"
     * @param downFlag          指定获取列表方向 true：获取所有子级树状列表；false：获取所有父级列表
     * @param showAllStatusFlag true：显示字段status所有状态的数据；false：只显示字段 status = 1 状态的数据 (获取父级列表时，一般showAllStatusFlag均应设为true)
     * @return List
     * @author JFnice
     */
    <T> List<M> queryAllRelativeList(T id, String fields, boolean downFlag, boolean showAllStatusFlag);

    /**
     * Model新增到数据库
     *
     * @param m 需新增的Model
     * @return boolean
     * @author JFnice
     */
    boolean save(M m);

    /**
     * Model更新到数据库
     *
     * @param m 需更新的Model
     * @return boolean
     * @author JFnice
     */
    boolean update(M m);

    /**
     * 删除数据
     *
     * @param id           主键值
     * @param isRealDelete true：真实删除数据库数据；false：逻辑删除数据库数据 SET del = 1
     * @return int 受影响行数
     * @author JFnice
     */
    <T> boolean deleteById(T id, boolean isRealDelete);

    /**
     * 批量删除数据
     *
     * @param ids          主键数组
     * @param isRealDelete true：真实删除数据库数据；false：逻辑删除数据库数据 SET del = 1
     * @return int[] 受影响行数
     * @author JFnice
     */
    <T> int[] batchDelete(T[] ids, boolean isRealDelete);

    /**
     * 切换字段值，即 1 和 0 互换。如：status = 1 <-- 相互切换 --> status = 0
     *
     * @param id    主键
     * @param field 字段
     * @return int 受影响行数
     * @author JFnice
     */
    <T> int toggleField(T id, String field);

    /**
     * 批量设置字段值
     *
     * @param field 字段名
     * @param value 字段值
     * @param ids   主键数组
     * @return int[] 受影响行数
     * @author JFnice
     */
    <T> int[] batchSetFieldValue(T[] ids, String field, Object value);

    /**
     * 批量设置sort字段值进行排序
     *
     * @param map 由 “id => sort”构成
     * @return int[] 受影响行数
     * @author JFnice
     */
    <K, V> int[] sort(Map<K, V> map);

    /**
     * id数组转换成以“,”分割的id集合字符串  [ id去重 ]
     *
     * @param ids 主键id数组
     * @return String
     * @author JFnice
     */
    <T> String ids2Str(T[] ids);

    /**
     * 将Model的primaryKey（及parentKey）转换成String型
     * 用途：对于Long型key，当其值过大时，转成json传到前端js处理时会失精度，需转成String型处理
     * 提示：通过Snowflake算法生成的Long型字段需要注意转换
     */
    void key2Str(M m);

    /**
     * 将Model列表的primaryKey（及parentKey）转换成String型
     * 用途：对于Long型key，当其值过大时，转成json传到前端js处理时会失精度，需转成String型处理
     * 提示：通过Snowflake算法生成的Long型字段需要注意转换
     */
    void key2Str(List<M> mList);

    /**
     * modelList转换成 id -> model 键值对Map
     *
     * @param mList Model的List集合
     * @return Map
     * @author JFnice
     */
    <T> Map<T, M> list2IdMap(List<M> mList);

    /**
     * 增加Model的所有子级树状列表属性childList（指定获取字段集） [ 子级树状列表只包含字段 status = 1 状态的数据 ]
     *
     * @param m      指定Model，如: m = new Model().set("id", 0L) 则获取 pid = 0L 的所有子级
     * @param fields 指定获取的字段集，至少应包含 id、pid、sort 三个字段，如："id, pid, name, sort"
     * @author JFnice
     */
    void addAttrChildList(M m, String fields);

    /**
     * 增加Model的所有子级树状列表属性childList（指定获取字段集）
     *
     * @param m               指定Model，如: m = new Model().set("id", 0L) 则获取 pid = 0L 的所有子级
     * @param fields          指定获取的字段集，至少应包含 id、pid、sort 三个字段，如："id, pid, name, sort"
     * @param isShowAllStatus true：子级树状列表显示字段status所有状态的数据；false：子级树状列表只显示字段 status = 1 状态的数据
     * @author JFnice
     */
    void addAttrChildList(M m, String fields, boolean isShowAllStatus);

    /**
     * 增加顶层Model的子级树状列表属性childList
     *
     * @param topM  指定顶层Model
     * @param mList 指定要构成树状的列表 [ mList至少应包含 id、pid、sort 三个字段，如："id, pid, name, sort" ]
     * @author JFnice
     */
    void buildTree(M topM, List<M> mList);

    /**
     * 子级树状列表根据sortComparator类进行迭代排列
     *
     * @author JFnice
     */
    void sortChildList(List<M> mList);

    /**
     * 自顶向下进行排序
     *
     * @author JFnice
     */
    void sortParentList(List<M> mList);

    void sortParentListHandle(M m, List<M> mList);

    /**
     * 检验Model字段的唯一性，多字段可模拟联合主键的唯一性检验功能。
     * 如果model中不带有任何一个指定的fields字段，则不进行数据校验直接返回true；
     * 如果model中带有部分指定的fields字段，则只校验model存在的fields字段。
     *
     * @param m      指定Model
     * @param fields 一个或多个字段名
     * @return boolean
     * @author JFnice
     */
    boolean isUnique(M m, String... fields);

    /**
     * 检验Model是否有子级数据
     *
     * @param m 指定Model
     * @return boolean
     * @author JFnice
     */
    boolean hasChild(M m);
}
