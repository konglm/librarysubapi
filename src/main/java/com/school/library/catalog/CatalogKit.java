package com.school.library.catalog;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.jfnice.model.Catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 图书目录辅助类
 * @Author jsy
 * @Date 2020/3/17
 * @Version V1.0
 **/

public class CatalogKit {

    /**
     * 按照前端样式组装成树
     * @param rootList 根节点列表
     * @param map 所有节点(pid-list)映射列表
     * @return
     */
    public static JSONArray generateTree(List<Catalog> rootList, Map<Long, List<Catalog>> map){
        JSONArray treeArray = new JSONArray();
        if(null!= rootList && !rootList.isEmpty() && null!= map && !map.isEmpty()){
            rootList.forEach(r -> {
                JSONObject rootJson = new JSONObject();
                rootJson.put("title", r.getCatalogNo() + " " + r.getCatalogName());
                rootJson.put("catalog_no", Strings.nullToEmpty(r.getCatalogNo()));
                rootJson.put("catalog_name", r.getCatalogName());
                rootJson.put("key", r.getPid() + "-" + r.getId());
                rootJson.put("pid", r.getPid());
                rootJson.put("id", r.getId());
                rootJson.put("sort", r.getSort());
                if(null!= map.get(r.getId())){
                    JSONArray childArray = new JSONArray();
                    List<Catalog> childList = map.get(r.getId());
                    if(null!= childList && !childList.isEmpty()){
                        childArray = generateTree(childList, map);
                        rootJson.put("children", childArray);
                    }
                }
                treeArray.add(rootJson);
            });
        }
        return treeArray;
    }

}
