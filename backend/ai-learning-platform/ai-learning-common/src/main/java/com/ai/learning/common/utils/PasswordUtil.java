package com.ai.learning.common.utils;

import java.security.MessageDigest;
import java.util.UUID;

public class PasswordUtil {

    public static String encrypt(String password) {
        return md5(password);
    }

    public static boolean matches(String rawPassword, String encryptedPassword) {
        return encrypt(rawPassword).equals(encryptedPassword);
    }

    public static String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return str;
        }
    }
}
