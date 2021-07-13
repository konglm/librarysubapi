package com.school.api.model;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BookList implements Serializable {

    private static final long serialVersionUID = 1L;

    private String totalCnt;

    private String totalAmount;

    private Page<Record> list;
}
