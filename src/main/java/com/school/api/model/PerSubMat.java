package com.school.api.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PerSubMat implements Serializable {

    private static final long serialVersionUID = 1L;

    private String percode;
    private String subcode;
    private String matercodes;

}
