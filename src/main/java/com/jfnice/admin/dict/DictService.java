package com.jfnice.admin.dict;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.Dict;

import java.util.Map;

public class DictService extends JFniceBaseService<Dict> {

    public boolean save(Dict dict) {
        if (!isUnique(dict, "tag", "k")) {
            throw new ErrorMsg(" 标签 - 键名 已存在！");
        }

        boolean flag = super.save(dict);
        DictKit.clear();
        return flag;
    }

    public boolean update(Dict dict) {
        if (!isUnique(dict, "tag", "k")) {
            throw new ErrorMsg(" 标签 - 键名 已存在！");
        }

        boolean flag = super.update(dict);
        DictKit.clear();
        return flag;
    }

    public boolean deleteById(long dictId, boolean isRealDelete) {
        boolean flag = super.deleteById(dictId, isRealDelete);
        DictKit.clear();
        return flag;
    }

    public <K, V> int[] sort(Map<K, V> map) {
        int[] intArr = super.sort(map);
        DictKit.clear();
        return intArr;
    }

    public void addRole(long dictId, long roleId) {
        Db.save("dict_user_type_role", new Record().set("dict_id", dictId).set("role_id", roleId));
    }

    public void updateRole(long dictId, long roleId) {
        clearRole(dictId);
        addRole(dictId, roleId);
    }

    public void clearRole(long dictId) {
        String sql = Db.getSql("Dict.clearRole");
        Db.update(sql, dictId);
    }

}