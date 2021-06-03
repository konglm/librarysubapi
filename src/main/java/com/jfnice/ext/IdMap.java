package com.jfnice.ext;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Okv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfnice.commons.CacheName;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.j2cache.J2CacheKit;
import com.jfnice.kit.CopyKit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class IdMap<M extends Model<M>, S extends JFniceBaseService<M>> {

    private S srv;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public IdMap() {
        try {
            Type type = getClass().getGenericSuperclass();
            ParameterizedType pt = (ParameterizedType) type;
            Type[] ts = pt.getActualTypeArguments();
            this.srv = (S) ((Class) ts[1]).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Object, M> getMap() {
        return getMap(false);
    }

    public String toJsonMap() {
        return toJsonMap(null);
    }

    public String toJsonMap(String fields) {
        Map<Object, M> map = getMap();
        if (map == null) {
            return null;
        }

        if (StrKit.notBlank(fields) && !fields.contains("*")) {
            String[] fieldArr = fields.replace(" ", "").split(",");
            Set<String> fieldSet = new HashSet<String>(Arrays.asList(fieldArr));
            Map<Object, M> copyMap = CopyKit.deepCopy(map);
            Map<Object, M> filterMap = new LinkedHashMap<Object, M>();
            for (Map.Entry<Object, M> entry : copyMap.entrySet()) {
                M m = entry.getValue();
                for (String attr : m._getAttrNames()) {
                    if (!fieldSet.contains(attr)) {
                        m.remove(attr);
                    }
                }
                filterMap.put(m.get(srv.getPrimaryKey()), m);
            }
            return JsonKit.toJson(filterMap);
        } else {
            return JsonKit.toJson(map);
        }
    }

    public String toJsonArray() {
        return toJsonArray(null);
    }

    public String toJsonArray(String fields) {
        Map<Object, M> map = getMap();
        if (map == null) {
            return null;
        }

        List<M> mList = new ArrayList<M>();
        for (Map.Entry<Object, M> entry : map.entrySet()) {
            mList.add(entry.getValue());
        }

        if (StrKit.notBlank(fields) && !fields.contains("*")) {
            String[] fieldArray = fields.replace(" ", "").split(",");
            Set<String> fieldSet = new HashSet<String>(Arrays.asList(fieldArray));
            List<M> copyList = CopyKit.deepCopy(mList);
            for (M m : copyList) {
                for (String attr : m._getAttrNames()) {
                    if (!fieldSet.contains(attr)) {
                        m.remove(attr);
                    }
                }
            }
            return JsonKit.toJson(copyList);
        }

        return JsonKit.toJson(mList);
    }

    public M get(Object id) {
        return getAllDataMap().get(id);
    }

    public Object attr(Object id, String attr) {
        return get(id).get(attr);
    }

    public String name(Object id) {
        if (getAllDataMap().get(id) != null) {
            return (String) getAllDataMap().get(id).get("name");
        }
        return null;
    }

    public void clear() {
        String key = getClass().getName();
        J2CacheKit.remove(CacheName.ALL_ID_MAP, key);
        J2CacheKit.remove(CacheName.NORMAL_ID_MAP, key);
    }

    protected Map<Object, M> getAllDataMap() {
        return getMap(true);
    }

    /**
     * 所有数据列表，包括已删除及所有状态
     */
    protected List<M> queryList() {
        return queryList("*", null);
    }

    /**
     * 一般用来重写子类的排序
     */
    protected List<M> queryList(String fields, String orderBy) {
        Okv whereOkv = null;
        //不能直接return srv.queryList(fields, null, orderBy)，因为当子类Service出现3个参数的queryList时，可能会导致调用不对应！
        return srv.queryList(fields, whereOkv, orderBy);
    }

    private Map<Object, M> getMap(boolean showAllDataFlag) {
        String key = getClass().getName();
        if (J2CacheKit.get(CacheName.ALL_ID_MAP, key) == null) {
            Map<Object, M> allDataIdMap = new LinkedHashMap<Object, M>();
            Map<Object, M> normalDataIdMap = new LinkedHashMap<Object, M>();
            List<M> mList = queryList();
            if (mList != null) {
                for (M m : mList) {
                    if (m.getInt("status") == 1 && !m.getBoolean("del")) {
                        normalDataIdMap.put(m.get(srv.getPrimaryKey()), m);
                    }
                    allDataIdMap.put(m.get(srv.getPrimaryKey()), m);
                }
            }
            J2CacheKit.put(CacheName.ALL_ID_MAP, key, allDataIdMap);
            J2CacheKit.put(CacheName.NORMAL_ID_MAP, key, normalDataIdMap);
        }
        return showAllDataFlag ? J2CacheKit.get(CacheName.ALL_ID_MAP, key) : J2CacheKit.get(CacheName.NORMAL_ID_MAP, key);
    }

}
