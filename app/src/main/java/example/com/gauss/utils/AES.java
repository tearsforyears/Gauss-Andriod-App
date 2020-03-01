package example.com.gauss.utils;


import example.com.gauss.settings;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    /**
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private static String sKey = settings.SKEY;
    private static String ivParameter = settings.IVPARAMETER;

    public static void main(String[] args) {
        String raw = "你好";

        try {
            String encryptRaw = encode(raw);
            System.out.println("加密串:" + encryptRaw);
            String decryptRaw = decode(encryptRaw);
            System.out.println("解秘串:" + decryptRaw);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String encode(String str) throws Exception {
        if (str != null) {
            return encrypt(str);
        } else {
            return null;
        }
    }

    public static String decode(String str) throws Exception {
        if (str != null) {
            return decrypt(str);
        } else {
            return null;
        }
    }

    public static String encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CFB/PKCS5Padding");
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        // return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码。
        return new String(Base64.getEncoder().encode(encrypted), "UTF-8");
    }

    // 解密
    public static String decrypt(String sSrc) throws Exception {
        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CFB/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            // byte[] encrypted1 = new
            // BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
            byte[] encrypted1 = Base64.getDecoder().decode(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }
}

