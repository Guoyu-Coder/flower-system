package com.ai.learning.util;

import cn.hutool.crypto.SecureUtil;

public class PasswordUtil {

    public static String encrypt(String password) {
        return SecureUtil.md5(password);
    }

    public static boolean matches(String rawPassword, String encryptedPassword) {
        return encrypt(rawPassword).equals(encryptedPassword);
    }
}
