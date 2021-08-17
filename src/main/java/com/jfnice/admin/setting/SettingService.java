package com.jfnice.admin.setting;

import com.Start;
import com.alibaba.fastjson.JSONException;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfnice.commons.CacheName;
import com.jfnice.ext.Column;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.cache.JsyCacheKit;
import com.jfnice.model.Setting;

import java.util.*;

public class SettingService {

    private static final String SEPARATOR = "#";

    public Map<String, String> getDefaultKeyTitleMap(String access) {
        Map<String, String> fieldTitleMap = Start.JFniceDevMode ? null : JsyCacheKit.get(CacheName.DEFAULT_KEY_TITLE, access);
        if (fieldTitleMap == null) {
            fieldTitleMap = new LinkedHashMap<String, String>();
            Setting setting = getDefaultSetting(access);
            for (Column column : setting.getColumnList()) {
                fieldTitleMap.put(column.getKey(), column.getTitle());
            }
            JsyCacheKit.put(CacheName.DEFAULT_KEY_TITLE, access, fieldTitleMap);
        }
        return fieldTitleMap;
    }

    public Map<String, String> getDefaultFieldTitleMap(String access) {
        Map<String, String> fieldTitleMap = Start.JFniceDevMode ? null : JsyCacheKit.get(CacheName.DEFAULT_FIELD_TITLE, access);
        if (fieldTitleMap == null) {
            fieldTitleMap = new LinkedHashMap<String, String>();
            Setting setting = getDefaultSetting(access);
            for (Column column : setting.getColumnList()) {
                if (column.getField()) {
                    fieldTitleMap.put(column.getKey(), column.getTitle());
                }
            }
            JsyCacheKit.put(CacheName.DEFAULT_FIELD_TITLE, access, fieldTitleMap);
        }
        return fieldTitleMap;
    }

    public Setting getCurrentSetting(String access) {
        String key = access + SEPARATOR + CurrentUser.getUserCode();
        Setting setting = Start.JFniceDevMode ? null : JsyCacheKit.get(CacheName.SETTING, key);
        if (setting == null) {
            setting = Setting.dao.findByIds(access, CurrentUser.getUserCode());
            if (setting == null) {
                setting = getDefaultSetting(access);
            } else {
                Map<String, Column> keyColumnMap = new HashMap<String, Column>();
                for (Column column : setting.getColumnList()) {
                    if (column.getDisplay() == 1) {
                        keyColumnMap.put(column.getKey(), column);
                    }
                }

                Setting defaultSetting = getDefaultSetting(access);
                List<Column> columnList = defaultSetting.getColumnList();
                for (Column column : columnList) {
                    if (keyColumnMap.containsKey(column.getKey())) {
                        Column current = keyColumnMap.get(column.getKey());
                        column.setSort(current.getSort());
                        column.setWidth(current.getWidth());
                        column.setDisplay(current.getDisplay());
                    } else {
                        column.setDisplay(column.getDisplay() == 1 ? 0 : column.getDisplay());
                    }
                }
                setting.setColumnList(columnList);
                setting.setColumns(JsonKit.toJson(columnList));
                JsyCacheKit.put(CacheName.SETTING, key, setting);
            }
        }
        return setting;
    }

    public Setting getDefaultSetting(String access) {
        String key = access + SEPARATOR + 0L;
        Setting setting = Start.JFniceDevMode ? null : JsyCacheKit.get(CacheName.SETTING, key);
        if (setting == null) {
            setting = Setting.dao.findByIds(access, 0L);
            JsyCacheKit.put(CacheName.SETTING, key, setting);
        }
        return setting;
    }

    public void clearCache(String access, long userId) {
        String key = access + SEPARATOR + userId;
        JsyCacheKit.remove(CacheName.SETTING, key);
        if (userId == 0L) {
            Setting defaultSetting = getDefaultSetting(access);
            Set<String> keySet = new HashSet<String>();
            for (Column column : defaultSetting.getColumnList()) {
                keySet.add(column.getKey());
            }

            List<Setting> customSettingList = getCustomListByAccess(access);
            List<Setting> updateSettingList = new ArrayList<Setting>();
            for (Setting setting : customSettingList) {
                boolean flag = false;
                List<Column> columnList = setting.getColumnList();
                Iterator<Column> iter = columnList.iterator();
                while (iter.hasNext()) {
                    Column column = iter.next();
                    if (!keySet.contains(column.getKey())) {
                        flag = true;
                        iter.remove();
                    }
                }

                if (flag) {
                    setting.setColumns(JsonKit.toJson(columnList));
                    updateSettingList.add(setting);
                }
            }

            Db.batchUpdate(updateSettingList, updateSettingList.size());
            JsyCacheKit.removeAll(CacheName.SETTING);
            JsyCacheKit.remove(CacheName.DEFAULT_KEY_TITLE, access);
            JsyCacheKit.remove(CacheName.DEFAULT_FIELD_TITLE, access);
        }
    }

    public List<Setting> getCustomListByAccess(String access) {
        return Setting.dao.find(Db.getSql("Setting.getCustomListByAccess"), access);
    }

    public void deleteAllByAccess(String access) {
        Db.update(Db.getSql("Setting.deleteAllByAccess"), access);
        JsyCacheKit.removeAll(CacheName.SETTING);
        JsyCacheKit.remove(CacheName.DEFAULT_KEY_TITLE, access);
        JsyCacheKit.remove(CacheName.DEFAULT_FIELD_TITLE, access);
    }

    public Setting saveOrUpdate(Setting setting) {
        List<Column> columnList;
        try {
            columnList = setting.getColumnList();
        } catch (JSONException e) {
            throw new ErrorMsg("表头参数JSON格式有误！");
        }

        boolean hasAuto = false;
        int displayFieldCount = 0;
        Column autoColumn = null;
        for (Column column : columnList) {
            if (column.getDisplay() != 1) {
                continue;
            }

            if (column.getField()) {
                if (autoColumn == null
                        || autoColumn.getWidth().getValue() == null
                        || (column.getWidth().getValue() != null && column.getWidth().getValue() > autoColumn.getWidth().getValue())
                ) {
                    autoColumn = column;
                }
                displayFieldCount++;
            }

            if ("auto".equals(column.getWidth().getType())) {
                if (hasAuto) {
                    column.getWidth().setType("px");
                    column.getWidth().setValue(100);
                }
                hasAuto = true;
            }
        }
        if (displayFieldCount == 0) {
            throw new ErrorMsg("请至少保留1个关键字段！");
        }
        if (!hasAuto) {
            autoColumn.getWidth().setType("auto");
            autoColumn.getWidth().setValue(null);
        }
        setting.setColumns(JsonKit.toJson(columnList));

        if (setting.getUserId() > 0L) {
            Setting defaultSetting = getDefaultSetting(setting.getAccess());
            for (Column column : columnList) {
                boolean flag = false;
                for (Column col : defaultSetting.getColumnList()) {
                    if (column.getKey().equals(col.getKey())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    throw new ErrorMsg("表头key参数有误！");
                }
            }
        }

        Setting sysSetting = Setting.dao.findByIds(setting.getAccess(), setting.getUserId());
        if (sysSetting == null) {
            setting.save();
        } else {
            setting.update();
            clearCache(setting.getAccess(), setting.getUserId());
        }
        return setting;
    }

}