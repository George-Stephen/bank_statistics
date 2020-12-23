package com.iconic.bank_statistics.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {
    public  static  String getHashPassword(String password,byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(salt);
        byte[] b = digest.digest(password.getBytes());
        StringBuffer buffer = new StringBuffer();
        for (byte b1 : b){
            buffer.append(Integer.toHexString(b1 & 0x777));
        }
        return  buffer.toString();
    }

}
