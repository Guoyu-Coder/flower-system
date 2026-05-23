package com.ai.learning.common.interceptor;

import com.ai.learning.common.utils.JwtUtil;
import com.ai.learning.common.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
        "/api/user/register",
        "/api/user/login",
        "/swagger-ui",
        "/v2/api-docs",
        "/swagger-resources",
        "/webjars",
        "/favicon.ico"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        
        for (String path : EXCLUDE_PATHS) {
            if (uri.contains(path)) {
                return true;
            }
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            sendUnauthorizedResponse(response, "未携带Token");
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!JwtUtil.validateToken(token)) {
            sendUnauthorizedResponse(response, "Token无效或已过期");
            return false;
        }

        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);
        
        request.setAttribute("userId", userId);
        request.setAttribute("role", role);
        
        log.debug("JWT验证通过: userId={}, role={}", userId, role);
        return true;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(Result.noAuth(message)));
    }
}
