package com.school.api.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Access implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long schid;
    private String uid;
    private String upw;
    private Short utp;
    private String ulgt;

}
