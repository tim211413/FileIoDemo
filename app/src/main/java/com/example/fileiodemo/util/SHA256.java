package com.example.fileiodemo.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    // SHA256 加密傳入的資料
    public static String shaEncrypt(byte[] strSrc) {

        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc;
        try {
            md = MessageDigest.getInstance("SHA-256");// 將此換成SHA-1、SHA-512、SHA-384等參數
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    // 將傳入的 byte array 轉成 16進
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

}
