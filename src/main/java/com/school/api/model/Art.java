package com.school.api.model;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class Art implements Serializable {
    private static final long serialVersionUID = 1L;

    @JSONField(name = "art_code")
    private String artCode;
    @JSONField(name = "art_name")
    private String artName;
    private List<Sub> subs;
}
