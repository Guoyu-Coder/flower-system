package com.ai.learning.agent.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ai.learning.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DeepSeekClient {

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.base.url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${deepseek.model:deepseek-chat}")
    private String model;

    @Value("${deepseek.temperature:0.7}")
    private double temperature;

    @Value("${deepseek.max_tokens:2000}")
    private int maxTokens;

    private final OkHttpClient client;

    public DeepSeekClient() {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    public String chat(List<Map<String, String>> messages) {
        return chat(messages, model);
    }

    public String chat(List<Map<String, String>> messages, String modelName) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("messages", messages);
            requestBody.put("temperature", temperature);
            requestBody.put("max_tokens", maxTokens);

            RequestBody body = RequestBody.create(
                JSON.toJSONString(requestBody),
                MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                .url(baseUrl + "/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("DeepSeek API error: {} - {}", response.code(), response.message());
                    throw BusinessException.of("AI服务请求失败: " + response.message());
                }

                String responseBody = response.body().string();
                JSONObject jsonResponse = JSON.parseObject(responseBody);
                
                if (jsonResponse.containsKey("error")) {
                    throw BusinessException.of("AI服务错误: " + jsonResponse.getString("error"));
                }

                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    return message.getString("content");
                }

                throw BusinessException.of("AI服务返回数据格式错误");
            }
        } catch (IOException e) {
            log.error("DeepSeek API IO error", e);
            throw BusinessException.of("AI服务连接失败: " + e.getMessage());
        }
    }

    public String chatWithSystemPrompt(String systemPrompt, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        return chat(messages);
    }

    public String chatWithContext(List<Map<String, String>> history, String newMessage) {
        List<Map<String, String>> messages = new ArrayList<>(history);
        
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", newMessage);
        messages.add(userMsg);

        return chat(messages);
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}
