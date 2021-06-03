package com.jfnice.core;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.*;
import com.jfnice.ext.CondPara;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class JFniceBaseService<M extends Model<M>> implements JFniceBaseSer<M> {

    /**
     * 批量保存时最大的数值
     */
    public int maxBatchSize = 1024;
    private M dao;
    @SuppressWarnings("rawtypes")
    private Class modelClass;
    private String parentKey;

    /**
     * 构造函数
     *
     * @author JFnice
     */
    public JFniceBaseService() {
        this(null);
    }

    /**
     * 构造函数
     *
     * @param parentKey 主键的父级字段名
     * @author JFnice
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public JFniceBaseService(String parentKey) {
        try {
            Class clazz = getClass();
            if (clazz.getName().indexOf("$$EnhancerBy") != -1) {
                clazz = clazz.getSuperclass();
            }
            Type type = clazz.getGenericSuperclass();
            ParameterizedType pt = (ParameterizedType) type;
            Type[] ts = pt.getActualTypeArguments();
            this.modelClass = (Class) ts[0];
            this.dao = (M) this.modelClass.newInstance();
            this.parentKey = StrKit.isBlank(parentKey) ? "pid" : parentKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对应Table类
     *
     * @return Table
     * @author JFnice
     */
    public Table getTable() {
        return TableMapping.me().getTable(dao.getClass());
    }

    /**
     * 获取对应数据库表名
     *
     * @return String
     * @author JFnice
     */
    public String getTableName() {
        return getTable().getName();
    }

    /**
     * 获取主键名
     *
     * @return String
     * @author JFnice
     */
    public String getPrimaryKey() {
        String[] primaryKeys = getTable().getPrimaryKey();
        if (primaryKeys.length != 1) {
            throw new RuntimeException("JFniceBaseService不支持联合主键！");
        }
        return primaryKeys[0];
    }

    /**
     * 获取主键类型
     *
     * @return Class
     * @author JFnice
     */
    public Class<?> getPrimaryKeyType() {
        return getTable().getColumnType(getPrimaryKey());
    }

    /**
     * 获取主键的父级字段名
     *
     * @return Class
     * @author JFnice
     */
    public String getParentKey() {
        return parentKey;
    }

    /**
     * 获取包含所有字段的Model
     *
     * @param id 主键值
     * @return Model
     * @author JFnice
     */
    public <T> M queryById(T id) {
        return queryById(id, "*");
    }

    /**
     * 获取指定字段集的Model
     *
     * @param id     主键值
     * @param fields 指定获取的字段集，如："id, name"
     * @return Model
     * @author JFnice
     */
    public <T> M queryById(T id, String fields) {
        Kv kv = Kv.by("fields", fields)
                .set("tableName", getTableName())
                .set("primaryKey", getPrimaryKey())
                .set("id", id);
        SqlPara sqlPara = Db.getSqlPara("JFniceBase.queryById", kv);
        return dao.findFirst(sqlPara);
    }

    /**
     * 获取指定字段集的Model列表
     *
     * @param ids    主键数组
     * @param fields
     * @return List
     * @author JFnice
     */
    public <T> List<M> queryListByIds(T[] ids, String fields) {
        return queryListByIds(ids, fields, null);
    }

    /**
     * 获取指定字段集的Model列表
     *
     * @param ids    主键数组
     * @param fields
     * @param orders
     * @return List
     * @author JFnice
     */
    public <T> List<M> queryListByIds(T[] ids, String fields, String orders) {
        if (ids == null || ids.length == 0) {
            return new ArrayList<M>();
        }

        Kv kv = Kv.by("fields", fields)
                .set("tableName", getTableName())
                .set("primaryKey", getPrimaryKey())
                .set("idsStr", ids2Str(ids))
                .set("orders", orders);
        return dao.find(Db.getSqlPara("JFniceBase.queryListByIds", kv));
    }

    /**
     * 获取指定字段集的Model列表 [ 只包含字段 status = 1 状态的数据 ]
     *
     * @param fields 指定获取的字段集，如："id, name"
     * @return List
     * @author JFnice
     */
    public List<M> queryList(String fields) {
        return queryList(fields, new String());
    }

    /**
     * 获取指定字段集及排序的Model列表  [ 只包含字段 status = 1 状态的数据 ]
     *
     * @param fields 指定获取的字段集，如："id, name"
     * @param orders 自定义排序字符串，如： "id DESC, name ASC"
     * @return List
     * @author JFnice
     */
    public List<M> queryList(String fields, String orders) {
        Okv conditions = Okv.by("del = ", 0)
                .set("status = ", 1);
        return queryList(fields, conditions, orders);
    }

    /**
     * 获取指定字段集的Model条件列表
     *
     * @param fields     指定获取的字段集，如："id, name"
     * @param conditions 自定义条件的Okv，如id>100： conditions.set("id > ", 100)
     * @return List
     * @author JFnice
     */
    public List<M> queryList(String fields, Okv conditions) {
        return queryList(fields, conditions, null);
    }

    /**
     * 获取指定字段集及排序的Model条件列表
     *
     * @param fields     指定获取的字段集，如："id, name"
     * @param conditions 自定义条件的Okv，如id>100： conditions.set("id > ", 100)
     * @param orders     自定义排序字符串，如： "id DESC, name ASC"
     * @return List
     * @author JFnice
     */
    public List<M> queryList(String fields, Okv conditions, String orders) {
        Kv kv = Kv.by("fields", fields)
                .set("tableName", getTableName())
                .set("primaryKey", getPrimaryKey())
                .set("conditions", conditions)
                .set("orders", orders);
        return dao.find(Db.getSqlPara("JFniceBase.queryPageOrList", kv));
    }

    /**
     * 获取Model列表
     *
     * @param condPara 指定condPara参数
     * @return Page
     * @author JFnice
     */
    public List<M> queryList(CondPara condPara) {
        return queryList(condPara.getFields(), condPara.getConditions(), condPara.getOrders());
    }

    /**
     * 获取Model分页
     *
     * @param condPara 指定condPara参数
     * @return Page
     * @author JFnice
     */
    public Page<M> queryPage(CondPara condPara) {
        condPara.set("tableName", getTableName())
                .set("primaryKey", getPrimaryKey());
        SqlPara sqlPara = Db.getSqlPara("JFniceBase.queryPageOrList", condPara.toKv());
        return dao.paginate(condPara.getPageNumber(), condPara.getPageSize(), sqlPara);
    }

    /**
     * 获取包含所有字段的Model子级列表  [ 只包含字段 status = 1 状态的数据 ]
     *
     * @param id     主键值
     * @param fields 指定获取的字段集，如："id, name"
     * @return List
     * @author JFnice
     */
    public <T> List<M> queryChildList(T id, String fields) {
        return queryChildList(id, fields, false);
    }

    /**
     * 获取指定字段集的Model子级列表
     *
     * @param id              主键值
     * @param fields          指定获取的字段集，如："id, name"
     * @param isShowAllStatus true：显示字段status所有状态的数据；false：只显示字段 status = 1 状态的数据
     * @return List
     * @author JFnice
     */
    public <T> List<M> queryChildList(T id, String fields, boolean isShowAllStatus) {
        Okv okv = Okv.by("del = ", 0)
                .set(getParentKey() + " = ", id);
        if (!isShowAllStatus) {
            okv.set("status = ", 1);
        }
        return queryList(fields, okv);
    }

    /**
     * 获取指定字段集的Model所有子级树状列表及其自身  [ 只包含字段 status = 1 状态的数据 ]
     *
     * @param id     主键值
     * @param fields 指定获取的字段集，如："id, name"
     * @return List
     * @author JFnice
     */
    public <T> List<M> queryAllChildList(T id, String fields) {
        return queryAllChildList(id, fields, false);
    }

    /**
     * 获取指定字段集的Model所有子级树状列表及其自身
     *
     * @param id              主键值
     * @param fields          指定获取的字段集，如："id, name"
     * @param isShowAllStatus true：显示字段status所有状态的数据；false：只显示字段 status = 1 状态的数据
     * @return List
     * @author JFnice
     */
    public <T> List<M> queryAllChildList(T id, String fields, boolean isShowAllStatus) {
        return queryAllRelativeList(id, fields, true, isShowAllStatus);
    }

    /**
     * 获取Model的所有子级id及其自身的集合  [ 只包含字段 status = 1 状态的数据 ]
     *
     * @param id 主键值
     * @return List
     * @author JFnice
     */
    public <T> List<T> queryAllChildIds(T id) {
        List<T> idList = new ArrayList<T>();
        List<M> mList = queryAllChildList(id, getPrimaryKey());
        for (M m : mList) {
            idList.add(m.get(getPrimaryKey()));
        }
        return idList;
    }

    /**
     * 获取指定字段集的Model所有父级顺序列表（不包含其自身） [ List排序：自顶向下排序 ]
     *
     * @param id     主键值
     * @param fields 指定获取的字段集，如："id, name"
     * @return List
     * @author JFnice
     */
    public <T> List<M> queryParentList(T id, String fields) {
        List<M> mList = queryAllRelativeList(id, fields, false, true);
        sortParentList(mList);
        if (mList.size() > 0) {
            mList.remove(mList.size() - 1);
        }
        return mList;
    }

    /**
     * 获取Model的所有父级id的集合（不包含其自身）
     *
     * @param id 主键值
     * @return List
     * @author JFnice
     */
    public <T> List<T> queryParentIds(T id) {
        List<T> idList = new ArrayList<T>();
        List<M> mList = queryParentList(id, getPrimaryKey() + "," + getParentKey());
        for (M m : mList) {
            idList.add(m.get(getPrimaryKey()));
        }
        return idList;
    }

    /**
     * 获取最顶层Model [ 即  pid = 0 的Model ]
     *
     * @param id 主键值
     * @return Model
     * @author JFnice
     */
    public <T> M queryRootById(T id) {
        List<M> mList = queryParentList(id, "*");
        return null == mList ? null : mList.get(0);
    }

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
    public <T> List<M> queryAllRelativeList(T id, String fields, boolean downFlag, boolean showAllStatusFlag) {
        Kv kv = Kv.create()
                .set("tableName", getTableName())
                .set("primaryKey", getPrimaryKey())
                .set("parentKey", getParentKey())
                .set("id", id)
                .set("fields", fields)
                .set("downFlag", downFlag)
                .set("showAllStatusFlag", showAllStatusFlag);
        SqlPara sqlPara = Db.getSqlPara("JFniceBase.queryAllRelativeList", kv);
        return dao.find(sqlPara);
    }

    /**
     * Model新增到数据库
     *
     * @param m 需新增的Model
     * @return boolean
     * @author JFnice
     */
    public boolean save(M m) {
        return m.save();
    }

    /**
     * Model更新到数据库
     *
     * @param m 需更新的Model
     * @return boolean
     * @author JFnice
     */
    public boolean update(M m) {
        return m.update();
    }

    /**
     * 删除数据
     *
     * @param id           主键值
     * @param isRealDelete true：真实删除数据库数据；false：逻辑删除数据库数据 SET del = 1
     * @return int 受影响行数
     * @author JFnice
     */
    public <T> boolean deleteById(T id, boolean isRealDelete) {
        Kv kv = Kv.by("tableName", getTableName())
                .set("primaryKey", getPrimaryKey())
                .set("isRealDelete", isRealDelete);
        SqlPara sqlPara = Db.getSqlPara("JFniceBase.deleteById", kv);
        return Db.update(sqlPara.getSql(), id) >= 1;
    }

    /**
     * 批量删除数据
     *
     * @param ids          主键数组
     * @param isRealDelete true：真实删除数据库数据；false：逻辑删除数据库数据 SET del = 1
     * @return int[] 受影响行数
     * @author JFnice
     */
    public <T> int[] batchDelete(T[] ids, boolean isRealDelete) {
        List<Record> recordList = new ArrayList<Record>();

        for (T id : ids) {
            recordList.add(new Record().set(getPrimaryKey(), id));
        }

        if (recordList.size() > 0) {
            Kv kv = Kv.by("tableName", getTableName())
                    .set("primaryKey", getPrimaryKey())
                    .set("isRealDelete", isRealDelete);
            SqlPara sqlPara = Db.getSqlPara("JFniceBase.deleteById", kv);
            return Db.batch(sqlPara.getSql(), getPrimaryKey(), recordList, recordList.size());
        }

        return new int[0];
    }

    /**
     * 切换字段值，即 1 和 0 互换。如：status = 1 <-- 相互切换 --> status = 0
     *
     * @param id    主键
     * @param field
     * @return int 受影响行数
     * @author JFnice
     */
    public <T> int toggleField(T id, String field) {
        Kv kv = Kv.by("tableName", getTableName())
                .set("primaryKey", getPrimaryKey())
                .set("field", field);
        SqlPara sqlPara = Db.getSqlPara("JFniceBase.toggleField", kv);
        return Db.update(sqlPara.getSql(), id);
    }

    /**
     * 批量设置字段值
     *
     * @param field 字段名
     * @param value 字段值
     * @param ids   主键数组
     * @return int[] 受影响行数
     * @author JFnice
     */
    public <T> int[] batchSetFieldValue(T[] ids, String field, Object value) {
        List<Record> recordList = new ArrayList<Record>();
        for (T id : ids) {
            recordList.add(new Record().set(getPrimaryKey(), id).set(field, value));
        }
        return Db.batchUpdate(getTableName(), recordList, recordList.size());
    }

    /**
     * 批量设置sort字段值进行排序
     *
     * @param map 由 “id => sort”构成
     * @return int[] 受影响行数
     * @author JFnice
     */
    public <K, V> int[] sort(Map<K, V> map) {
        List<Record> recordList = new ArrayList<Record>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K id = entry.getKey();
            V sort = entry.getValue();
            if (id == null || sort == null) {
                continue;
            }
            recordList.add(new Record().set(getPrimaryKey(), id).set("sort", sort));
        }
        return Db.batchUpdate(getTableName(), recordList, recordList.size());
    }

    /**
     * id数组转换成以“,”分割的id集合字符串  [ id去重 ]
     *
     * @param ids 主键id数组
     * @return String
     * @author JFnice
     */
    public <T> String ids2Str(T[] ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (Number.class.isAssignableFrom(getPrimaryKeyType())) {
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(ids[i].toString());
            }
        } else if (getPrimaryKeyType().equals(String.class)) {
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("'").append(ids[i].toString()).append("'");
            }
        }
        return sb.toString();
    }

    /**
     * 将Model的primaryKey（及parentKey）转换成String型
     * 用途：对于Long型key，当其值过大时，转成json传到前端js处理时会失精度，需转成String型处理
     * 提示：通过Snowflake算法生成的Long型字段需要注意转换
     */
    public void key2Str(M m) {
        m.put(getPrimaryKey(), m.getStr(getPrimaryKey()));
        if (getParentKey() != null) {
            m.put(getParentKey(), m.getStr(getParentKey()));
        }
    }

    /**
     * 将Model列表的primaryKey（及parentKey）转换成String型
     * 用途：对于Long型key，当其值过大时，转成json传到前端js处理时会失精度，需转成String型处理
     * 提示：通过Snowflake算法生成的Long型字段需要注意转换
     */
    public void key2Str(List<M> mList) {
        if (mList != null) {
            for (M m : mList) {
                key2Str(m);
            }
        }
    }

    /**
     * modelList转换成 id -> model 键值对Map
     *
     * @param mList Model的List集合
     * @return Map
     * @author JFnice
     */
    public <T> Map<T, M> list2IdMap(List<M> mList) {
        Map<T, M> map = new LinkedHashMap<T, M>();
        if (mList != null) {
            for (M m : mList) {
                map.put(m.get(getPrimaryKey()), m);
            }
        }
        return map;
    }

    /**
     * 增加Model的所有子级树状列表属性childList（指定获取字段集） [ 子级树状列表只包含字段 status = 1 状态的数据 ]
     *
     * @param m      指定Model，如: m = new Model().set("id", 0L) 则获取 pid = 0L 的所有子级
     * @param fields 指定获取的字段集，至少应包含 id、pid、sort 三个字段，如："id, pid, name, sort"
     * @return 无
     * @author JFnice
     */
    public void addAttrChildList(M m, String fields) {
        addAttrChildList(m, fields, false);
    }

    /**
     * 增加Model的所有子级树状列表属性childList（指定获取字段集）
     *
     * @param m               指定Model，如: m = new Model().set("id", 0L) 则获取 pid = 0L 的所有子级
     * @param fields          指定获取的字段集，至少应包含 id、pid、sort 三个字段，如："id, pid, name, sort"
     * @param isShowAllStatus true：子级树状列表显示字段status所有状态的数据；false：子级树状列表只显示字段 status = 1 状态的数据
     * @return 无
     * @author JFnice
     */
    public void addAttrChildList(M m, String fields, boolean isShowAllStatus) {
        List<M> mList = queryAllChildList(m.get(getPrimaryKey()), fields, isShowAllStatus);
        buildTree(m, mList);
    }

    /**
     * 增加顶层Model的子级树状列表属性childList
     *
     * @param topM  指定顶层Model
     * @param mList 指定要构成树状的列表 [ mList至少应包含 id、pid、sort 三个字段，如："id, pid, name, sort" ]
     * @return 无
     * @author JFnice
     */
    @SuppressWarnings("unchecked")
    public void buildTree(M topM, List<M> mList) {
        if (mList == null || mList.isEmpty()) {
            topM.put("children", new ArrayList<M>());
            return;
        }

        Map<Object, M> map = new HashMap<Object, M>();
        for (M m : mList) {
            if (m.get(getPrimaryKey()) == null || m.get(getParentKey()) == null || m.get("sort") == null) {
                throw new RuntimeException("字段参数至少应包含" + getPrimaryKey() + "、" + getParentKey() + "、sort");
            }
            map.put(m.get(getPrimaryKey()), m);
        }

        if ("0".equals(topM.getStr(getPrimaryKey()))) {
            if (topM.get(getParentKey()) == null) {
                topM.set(getParentKey(), topM.get(getPrimaryKey()));
            }

            if (!getPrimaryKeyType().equals(topM.get(getPrimaryKey()).getClass()) || !getPrimaryKeyType().equals(topM.get(getParentKey()).getClass())) {
                throw new RuntimeException("主键类型及主键的父级字段类型必须设置为: " + getPrimaryKeyType());
            }

            map.put(topM.get(getParentKey()), topM);
        }

        M root = null;
        for (Map.Entry<Object, M> entry : map.entrySet()) {
            M tmp = entry.getValue();

            if (tmp.get("children") == null) {
                tmp.put("children", new ArrayList<M>());
            }

            if (topM.get(getPrimaryKey()).equals(entry.getKey())) {
                root = tmp;
            } else {
                M temp = map.get(tmp.get(getParentKey()));
                if (temp != null) {
                    if (temp.get("children") == null) {
                        temp.put("children", new ArrayList<M>());
                    }
                    ((List<M>) temp.get("children")).add(tmp);
                }
            }
        }

        sortChildList((List<M>) root.get("children"));
        topM.put("children", (List<M>) root.get("children"));
    }

    /**
     * 子级树状列表排序规则类，优先根据字段sort升序排列，如果字段sort值相同则根据id升序排列
     *
     * @author JFnice
     */
    public class ChildComparator implements Comparator<M> {
        public int compare(M m1, M m2) {
            if (m1.getLong("sort").equals(m2.getLong("sort"))) {
                if (Number.class.isAssignableFrom(getPrimaryKeyType())) {
                    return m1.getLong(getPrimaryKey()) < m2.getLong(getPrimaryKey()) ? -1 : 1;
                } else if (getPrimaryKeyType().equals(String.class)) {
                    return m1.getStr(getPrimaryKey()).compareTo(m2.getStr(getPrimaryKey()));
                }
            }
            return m1.getLong("sort") < m2.getLong("sort") ? -1 : 1;
        }
    }

    /**
     * 子级树状列表根据sortComparator类进行迭代排列
     *
     * @return 无
     * @author JFnice
     */
    public void sortChildList(List<M> mList) {
        Collections.sort(mList, new ChildComparator());
        List<M> children;
        for (M m : mList) {
            children = m.get("children");
            if (children.size() > 0) {
                sortChildList(children);
            } else {
                m.remove("children");
            }
        }
    }

    /**
     * 自顶向下进行排序
     *
     * @return 无
     * @author JFnice
     */
    public void sortParentList(List<M> mList) {
        if (mList == null || mList.isEmpty()) {
            return;
        }

        Map<Object, M> map = new HashMap<Object, M>();
        for (M m : mList) {
            if (m.get(getPrimaryKey()) == null || m.get(getParentKey()) == null) {
                throw new RuntimeException("字段参数至少应包含" + getPrimaryKey() + "、" + getParentKey());
            }
            map.put(m.get(getParentKey()), m);
        }

        M root = null;
        for (Map.Entry<Object, M> entry : map.entrySet()) {
            M tmp = entry.getValue();
            if ("0".equals(tmp.getStr(getParentKey()))) {
                root = tmp;
            }

            M temp = map.get(tmp.get(getPrimaryKey()));
            if (temp != null) {
                tmp.put("child", temp);
            }
        }

        List<M> newList = new ArrayList<M>();
        sortParentListHandle(root, newList);
        mList.clear();
        mList.addAll(newList);
    }

    public void sortParentListHandle(M m, List<M> mList) {
        mList.add(m);
        M child = m.get("child");
        if (child != null) {
            m.remove("child");
            sortParentListHandle(child, mList);
        }
    }

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
    public boolean isUnique(M m, String... fields) {
        boolean flag = false;
        for (String field : fields) {
            if (m.get(field) != null) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            return true;
        }

        Kv kv = Kv.by("tableName", getTableName())
                .set("primaryKey", getPrimaryKey())
                .set("fields", fields)
                .set("m", m);
        SqlPara sqlPara = Db.getSqlPara("JFniceBase.isUnique", kv);
        return null == dao.findFirst(sqlPara);
    }

    /**
     * 检验Model是否有子级数据
     *
     * @param m 指定Model
     * @return boolean
     * @author JFnice
     */
    public boolean hasChild(M m) {
        Kv kv = Kv.by("tableName", getTableName())
                .set("parentKey", getParentKey())
                .set("pid", m.get(getPrimaryKey()));
        SqlPara sqlPara = Db.getSqlPara("JFniceBase.hasChild", kv);
        return null != dao.findFirst(sqlPara);
    }

}
