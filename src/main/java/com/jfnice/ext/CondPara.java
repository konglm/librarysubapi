package com.jfnice.ext;

import com.jfinal.aop.Aop;
import com.jfinal.json.Json;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.StrKit;
import com.jfnice.admin.setting.SettingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class CondPara extends Kv {

    private static final SettingService settingService = Aop.get(SettingService.class);

    public String getAccess() {
        return getAs("access");
    }

    public void setAccess(String access) {
        set("access", access);
    }

    public List<String> getExportableKeyList() {
        return getAs("exportableKeyList");
    }

    public void setExportableKeyList(List<String> exportableKeyList) {
        set("exportableKeyList", exportableKeyList);
    }

    public List<String> getKeyList() {
        return getAs("keyList");
    }

    public void setKeyList(List<String> keyList) {
        set("keyList", keyList);
    }

    public String getFields() {
        return getFields(false);
    }

    public void setFields(String fields) {
        set("fields", fields);
    }

    public String getFields(boolean alias) {
        String access = getAccess();
        if (StrKit.notBlank(access)) {
            List<String> keyList = getKeyList();
            List<String> exportableKeyList = getExportableKeyList();
            if ((keyList == null || keyList.isEmpty()) && (exportableKeyList == null || exportableKeyList.isEmpty())) {
                throw new RuntimeException("参数有误！");
            }

            List<String> allKeyList = new ArrayList<String>();
            if (keyList != null) {
                allKeyList.addAll(keyList);
            }
            if (exportableKeyList != null) {
                allKeyList.addAll(exportableKeyList);
            }

            StringBuilder sb = new StringBuilder();
            Map<String, String> fieldTitleMap = settingService.getDefaultFieldTitleMap(access);
            boolean first = true;
            for (String key : allKeyList) {
                if (!fieldTitleMap.containsKey(key)) {
                    continue;
                }

                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }

                sb.append(key);
                if (alias) {
                    sb.append(" AS '").append(key).append("'");
                }
            }
            return sb.toString();
        }
        return null;
    }

    public String getSortField() {
        return getAs("sortField");
    }

    public void setSortField(String sortField) {
        set("sortField", sortField);
    }

    public String getSortOrder() {
        return getAs("sortOrder");
    }

    public void setSortOrder(String sortOrder) {
        set("sortOrder", sortOrder);
    }

    public String getOrders() {
        String orders = getAs("orders");
        String sortField = getSortField();
        if (StrKit.isBlank(orders) && StrKit.notBlank(sortField)) {
            return sortField + ("ascend".equals(getSortOrder()) ? " ASC" : " DESC");
        }
        return orders;
    }

    public void setOrders(String orders) {
        set("orders", orders);
    }

    public Integer getPageSize() {
        if (get("pageSize") == null) {
            set("pageSize", 20);
        }
        return getAs("pageSize");
    }

    public void setPageSize(Integer pageSize) {
        set("pageSize", pageSize);
    }

    public Integer getPageNumber() {
        if (get("pageNumber") == null) {
            set("pageNumber", 1);
        }
        return getAs("pageNumber");
    }

    public void setPageNumber(Integer pageNumber) {
        set("pageNumber", pageNumber);
    }

    public Okv getConditions() {
        if (get("conditions") == null) {
            set("conditions", Okv.by("del = ", 0));
        }
        return getAs("conditions");
    }

    public void setConditions(Okv conditions) {
        set("conditions", conditions);
    }

    public CondPara addCondition(Okv conditions) {
        getConditions().set(conditions);
        return this;
    }

    public CondPara addCondition(String fieldCondition, Object value) {
        if (value != null) {
            getConditions().set(fieldCondition, value);
        }
        return this;
    }

    public CondPara setPara(String name, Object value) {
        set(name, value);
        return this;
    }

    public Kv toKv() {
        return toKv(false);
    }

    public Kv toKv(boolean alias) {
        return this.set("fields", getFields(alias))
                .set("conditions", getConditions())
                .set("orders", getOrders());
    }

    public String toString() {
        return Json.getJson().toJson(this.toKv());
    }

}