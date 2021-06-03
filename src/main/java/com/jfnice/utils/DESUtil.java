package com.jfnice.utils;

import com.jfinal.kit.HashKit;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Arrays;

/**
 * DES加密
 */
public class DESUtil {

    public static void main(String[] args) {
        try {
            byte[] key = HashKit.md5("jsy309").toUpperCase().substring(0, 8).getBytes();
            byte[] iv = HashKit.md5("jsy309").toUpperCase().substring(0, 8).getBytes();
            byte[] data = DESUtil.encrypt("schid:100005|uid:ceshi01|upw:123456|utp:0|ulgt:20180531171907".getBytes(), key, iv);
            System.out.println(Arrays.toString(data));
            System.out.println(Arrays.toString(key));
            System.out.println("加密：" + bytesToHexString(data));
            System.out.println("解密：" + new String(DESUtil.decrypt(data, key, iv)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * key不足8位则补足 , 尚未验证是不是这样天
     */
    public static byte[] fillKey(byte[] key) {
        int base = 8;//8位
        if (key.length % base != 0) {
            int groups = key.length / base + (key.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);//或者可能是：  Arrays.fill(temp, (byte)(key.length - key.length % base));
            System.arraycopy(key, 0, temp, 0, key.length);
            return temp;
        }
        return key;
    }

    public static String bytesToHexString(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    public static String jsyDecrypt(String text, String secretKey) {
        byte[] keyIv = HashKit.md5(secretKey).toUpperCase().substring(0, 8).getBytes();
        return new String(DESUtil.decrypt(hexStringToBytes(text), keyIv, keyIv));
    }

    /**
     * 加密
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) {
        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec param = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, param);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     */
    public static byte[] decrypt(byte[] data, byte[] key, byte[] iv) {
        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec param = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, param);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
