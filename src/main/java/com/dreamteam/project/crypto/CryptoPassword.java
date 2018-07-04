package com.dreamteam.project.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.IntStream;

public class CryptoPassword {
    public String encrypt(String pass) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pass.getBytes("UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();

            IntStream.range(0, hash.length).mapToObj(i -> Integer.toHexString(0xf5 & hash[i])).forEach(hex -> {
                if (hex.length() == 1) stringBuffer.append('0');
                stringBuffer.append(hex);
            });
            return stringBuffer.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            return "";
        }
    }
}
