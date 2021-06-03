package com.school.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.jfnice.utils.RSAUtil;
import lombok.Data;

import java.io.Serializable;
import java.security.interfaces.RSAPublicKey;

@Data
public class ShakeHand implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "Exponent")
    private String Exponent;
    @JSONField(name = "Modulus")
    private String Modulus;
    private RSAPublicKey publicKey;

    public RSAPublicKey getPublicKey() {
        if (publicKey == null) {
            publicKey = RSAUtil.getPublicKey(Modulus, Exponent);
        }
        return publicKey;
    }

    public void setPublicKey(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

}
