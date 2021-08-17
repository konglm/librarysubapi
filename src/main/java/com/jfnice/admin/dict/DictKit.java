package com.jfnice.admin.dict;

import com.jfinal.aop.Aop;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.cache.JsyCacheKit;
import com.jfnice.model.Dict;

import java.util.*;

/**
 * 字段辅助类
 */
public class DictKit {

    private static final long LIVE_TIME = 24*60*60L;

    private static final String CACHE_KEY_DICT_TAG_KEY = "JnDictTagKey";
    private static final String CACHE_KEY_DICT_TAG_VAL = "JnDictTagVal";

    private static final DictService dictService = Aop.get(DictService.class);

    /**
     * 获取字典里某个值的名称
     * @param tag 字典名
     * @param val 值
     * @return
     */
    public static String text(String tag, String val) {
        Dict dict = getTagValDictMap().get(tag).get(val);
        return dict != null ? dict.getLabel() : null;
    }

    /**
     * 获取字典里某个值的名称
     * @param tag 字典名
     * @param val 值
     * @return
     */
    public static String text(String tag, Integer val) {
        return val != null ? text(tag, String.valueOf(val)) : null;
    }

    /**
     * 获取字典里某个值的名称
     * @param tag 字典名
     * @param val 值
     * @return
     */
    public static String text(String tag, Short val) {
        return val != null ? text(tag, String.valueOf(val)) : null;
    }

    public static String style(String tag, String val) {
        Map<String, Dict> map = getTagValDictMap().get(tag);
        if (map == null) {
            return null;
        }
        Dict dict = map.get(val);
        return dict != null ? dict.getStyle() : null;
    }

    public static String style(String tag, Integer val) {
        return val != null ? style(tag, String.valueOf(val)) : null;
    }

    public static String style(String tag, Short val) {
        return val != null ? style(tag, String.valueOf(val)) : null;
    }

    /**
     * 获取字典里某个键的值
     * @param tag 字典名
     * @param key 键名
     * @return
     */
    public static String getValue(String tag, String key) {
        Map<String, Dict> map = getTagKeyDictMap().get(tag);
        if (map == null) {
            return null;
        }
        Dict dict = map.get(key);
        return dict != null ? dict.getV() : null;
    }

    /**
     * 获取字典里某个键的值，转成整型
     * @param tag 字典名
     * @param key 键名
     * @return
     */
    public static Integer getValueToInt(String tag, String key) {
        String val = getValue(tag, key);
        return val != null ? Integer.valueOf(val) : null;
    }

    /**
     * 获取字典里某个键的值，转成short
     * @param tag 字典名
     * @param key 键名
     * @return
     */
    public static Short getValueToShort(String tag, String key) {
        String val = getValue(tag, key);
        return val != null ? Short.valueOf(val) : null;
    }

    /**
     * 获取字典里所有的键-值map对象
     * @param tag 字典名
     * @return
     */
    public static LinkedHashMap<String, Dict> getMap(String tag) {
        return getTagValDictMap().get(tag);
    }

    /**
     * 获取字典里所有的字典对象列表
     * @param tag 字典名
     * @return
     */
    public static List<Dict> getList(String tag) {
        LinkedHashMap<String, Dict> map = getMap(tag);
        if (map == null) {
            return null;
        }

        List<Dict> dictList = new ArrayList<Dict>();
        for (Map.Entry<String, Dict> entry : map.entrySet()) {
            dictList.add(entry.getValue());
        }

        return dictList;
    }

    /**
     * 获取字典里所有键-值map对象的json字符串
     * @param tag 字典名
     * @return
     */
    public static String toJsonMap(String tag) {
        return JsonKit.toJson(getMap(tag));
    }

    /**
     * 获取字典里的所有对象列表的数组形式的字符串
     * @param tag 字典名
     * @return
     */
    public static String toJsonArray(String tag) {
        List<Dict> dictList = getList(tag);
        if (dictList == null) {
            return null;
        }
        return JsonKit.toJson(dictList);
    }

    /**
     * 将字段数据加载到缓存
     * @return
     */
    private static void reloadData() {
        Map<String, LinkedHashMap<String, Dict>> tagKeyDictMap = new HashMap<String, LinkedHashMap<String, Dict>>();
        Map<String, LinkedHashMap<String, Dict>> tagValDictMap = new HashMap<String, LinkedHashMap<String, Dict>>();
        List<Dict> dictList = dictService.queryList("*", "sort ASC");
        for (Dict dict : dictList) {
            if (!tagKeyDictMap.containsKey(dict.getTag())) {
                tagKeyDictMap.put(dict.getTag(), new LinkedHashMap<String, Dict>());
            }

            if (!tagValDictMap.containsKey(dict.getTag())) {
                tagValDictMap.put(dict.getTag(), new LinkedHashMap<String, Dict>());
            }

            tagKeyDictMap.get(dict.getTag()).put(dict.getK(), dict);
            tagValDictMap.get(dict.getTag()).put(dict.getV(), dict);
        }
        JsyCacheKit.put(CacheName.DICT, CACHE_KEY_DICT_TAG_KEY, tagKeyDictMap, LIVE_TIME);
        JsyCacheKit.put(CacheName.DICT, CACHE_KEY_DICT_TAG_VAL, tagValDictMap, LIVE_TIME);
    }

    private static Map<String, LinkedHashMap<String, Dict>> getTagKeyDictMap() {
        Map<String, LinkedHashMap<String, Dict>> tagKeyDictMap = JsyCacheKit.get(CacheName.DICT, CACHE_KEY_DICT_TAG_KEY);
        if (tagKeyDictMap == null) {
            reloadData();
            return JsyCacheKit.get(CacheName.DICT, CACHE_KEY_DICT_TAG_KEY);
        }
        return tagKeyDictMap;
    }

    private static Map<String, LinkedHashMap<String, Dict>> getTagValDictMap() {
        Map<String, LinkedHashMap<String, Dict>> tagKeyDictMap = JsyCacheKit.get(CacheName.DICT, CACHE_KEY_DICT_TAG_VAL);
        if (tagKeyDictMap == null) {
            reloadData();
            return JsyCacheKit.get(CacheName.DICT, CACHE_KEY_DICT_TAG_VAL);
        }
        return tagKeyDictMap;
    }

    /**
     * 清除缓存
     */
    public static void clear() {
        JsyCacheKit.removeAll(CacheName.DICT);
    }

}
