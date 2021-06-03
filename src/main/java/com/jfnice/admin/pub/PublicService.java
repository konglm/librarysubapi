package com.jfnice.admin.pub;

import com.jfinal.kit.HashKit;

public class PublicService {

    private static final String ENCRYPT_PREFIX = "#@_JFnice_@#";//加密前缀

    /*
     * 加密前缀
     *
     * @author JFnice
     */
    public String getEncryptPrefix() {
        return ENCRYPT_PREFIX;
    }

    /*
     * Sha256加密 （目前加密方式：前端先Md5加密，后台再Sha256加密）
     *
     * @author JFnice
     */
    public String sha256(String salt, String password) {
        return HashKit.sha256(salt.concat(password));
    }

}
