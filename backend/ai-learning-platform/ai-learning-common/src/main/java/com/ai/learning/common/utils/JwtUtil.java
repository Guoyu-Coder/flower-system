package com.ai.learning.common.utils;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtUtil {

    private static String SECRET = System.getenv().getOrDefault("JWT_SECRET", "");
    private static long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    public static void setSecret(String secret) {
        SECRET = secret;
    }

    public static void setExpireTime(long expireTime) {
        EXPIRE_TIME = expireTime;
    }

    public static String createToken(Long userId, String username, String role) {
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        
        return JWT.create()
            .withHeader(map)
            .withClaim("userId", userId)
            .withClaim("username", username)
            .withClaim("role", role)
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME))
            .withIssuedAt(new Date())
            .sign(Algorithm.HMAC256(SECRET));
    }

    public static DecodedJWT verify(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
        } catch (JWTVerificationException e) {
            log.error("JWT验证失败: {}", e.getMessage());
            return null;
        }
    }

    public static Long getUserId(String token) {
        DecodedJWT jwt = verify(token);
        if (jwt == null) {
            return null;
        }
        return jwt.getClaim("userId").asLong();
    }

    public static String getUsername(String token) {
        DecodedJWT jwt = verify(token);
        if (jwt == null) {
            return null;
        }
        return jwt.getClaim("username").asString();
    }

    public static String getRole(String token) {
        DecodedJWT jwt = verify(token);
        if (jwt == null) {
            return null;
        }
        return jwt.getClaim("role").asString();
    }

    public static boolean isTokenExpired(String token) {
        DecodedJWT jwt = verify(token);
        if (jwt == null) {
            return true;
        }
        return jwt.getExpiresAt().before(new Date());
    }

    public static boolean validateToken(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        return !isTokenExpired(token) && verify(token) != null;
    }
}
