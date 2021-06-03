package com.school.api.map;

import com.school.api.model.Sub;

import java.util.*;

/**
 * 包含分科的所有科目信息
 */
public class SubArtMap {

    public static final SubArtMap me = new SubArtMap();

    public Map<String, Sub> getMap() {
        Map<String, Sub> map = SubMap.me.getMap();
        ArtMap.me.getMap().values().forEach(art -> {
            Sub sub = new Sub();
            sub.setSubCode("art_" + art.getArtCode());
            sub.setSubName(art.getArtName());
            sub.setSubs(art.getSubs());
            map.put(sub.getSubCode(), sub);
        });
        return map;
    }

    public Sub get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getSubName();
        }
        return null;
    }

    public List<Sub> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

}
