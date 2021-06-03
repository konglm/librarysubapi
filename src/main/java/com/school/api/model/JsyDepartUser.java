package com.school.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JsyDepartUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String utid;
    private Long dptid;
    private String utname;
    private Short sex;
    private List<Dpt> udpts;

}
