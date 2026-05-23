package com.ai.learning.config;

import cn.hutool.core.util.StrUtil;
import com.ai.learning.util.JwtUtil;
import com.alibaba.fastjson.JSON;
import com.ai.learning.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token)) {
            sendUnauthorizedResponse(response, "未登录");
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            String userId = jwtUtil.getUserIdFromToken(token);
            String cachedToken = redisTemplate.opsForValue().get("token:" + userId);

            if (cachedToken == null || !cachedToken.equals(token)) {
                sendUnauthorizedResponse(response, "Token已失效");
                return false;
            }

            request.setAttribute("userId", Long.parseLong(userId));
            return true;
        } catch (Exception e) {
            sendUnauthorizedResponse(response, "无效的Token");
            return false;
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(JSON.toJSONString(ApiResponse.error(401, message)));
    }
}
