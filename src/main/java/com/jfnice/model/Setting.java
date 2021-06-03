package com.jfnice.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.jfnice.ext.Column;
import com.jfnice.model.base.BaseSetting;

import java.util.List;


@SuppressWarnings("serial")
public class Setting extends BaseSetting<Setting> {
    public static final Setting dao = new Setting().dao();

    @JSONField(serialize = false)
    public String columns;

    @JSONField(name = "columns")
    public List<Column> columnList;

    public List<Column> getColumnList() {
        return JSONObject.parseArray(getColumns(), Column.class);
    }

    public void setColumnList(List<Column> columnList) {
        setColumns(JSONObject.toJSONString(columnList));
    }

}
