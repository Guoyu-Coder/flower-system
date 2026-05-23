package com.ai.learning.service;

import com.ai.learning.dto.AiAnalyzeRequest;
import com.ai.learning.dto.AiGenerateRequest;
import com.ai.learning.entity.QuestionBank;
import com.ai.learning.entity.WrongQuestion;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DeepSeekService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.api-url}")
    private String apiUrl;

    @Value("${deepseek.model}")
    private String model;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .proxy(Proxy.NO_PROXY)
            .build();

    public List<QuestionBank> generateQuestions(AiGenerateRequest request) {
        String prompt = buildGeneratePrompt(request);

        try {
            String response = callDeepSeekApi(prompt);
            return parseGeneratedQuestions(response, request.getSubject());
        } catch (Exception e) {
            throw new RuntimeException("AI生成题目失败: " + e.getMessage());
        }
    }

    private String buildGeneratePrompt(AiGenerateRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下要求生成").append(request.getCount()).append("道题目：\n");
        prompt.append("科目：").append(request.getSubject()).append("\n");
        prompt.append("难度：").append(getDifficultyText(request.getDifficulty())).append("\n");

        if (request.getRequirement() != null && !request.getRequirement().isEmpty()) {
            prompt.append("特殊要求：").append(request.getRequirement()).append("\n");
        }

        prompt.append("\n题目类型包括：单选题、多选题、判断题、简答题");
        prompt.append("\n\n请以JSON数组格式返回，格式如下：");
        prompt.append("\n[{\"title\":\"题目标题\",\"content\":\"题目内容\",\"type\":1,\"difficulty\":").append(request.getDifficulty())
                .append(",\"answer\":\"正确答案\",\"analysis\":\"题目解析\"}]");
        prompt.append("\n其中type: 1-单选, 2-多选, 3-判断, 4-简答");

        return prompt.toString();
    }

    private String getDifficultyText(Integer difficulty) {
        switch (difficulty) {
            case 1: return "简单";
            case 2: return "中等";
            case 3: return "困难";
            default: return "中等";
        }
    }

    private List<QuestionBank> parseGeneratedQuestions(String response, String subject) {
        List<QuestionBank> questions = new ArrayList<>();

        try {
            JSONArray jsonArray = JSON.parseArray(response);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                QuestionBank question = new QuestionBank();
                question.setTitle(obj.getString("title"));
                question.setContent(obj.getString("content"));
                question.setType(obj.getInteger("type"));
                question.setDifficulty(obj.getInteger("difficulty"));
                question.setSubject(subject);
                question.setAnswer(obj.getString("answer"));
                question.setAnalysis(obj.getString("analysis"));
                question.setUsageCount(0);
                questions.add(question);
            }
        } catch (Exception e) {
            throw new RuntimeException("解析AI响应失败: " + e.getMessage());
        }

        return questions;
    }

    public String analyzeWrongAnswer(AiAnalyzeRequest request) {
        String prompt = buildAnalyzePrompt(request);

        try {
            return callDeepSeekApi(prompt);
        } catch (Exception e) {
            throw new RuntimeException("AI分析失败: " + e.getMessage());
        }
    }

    private String buildAnalyzePrompt(AiAnalyzeRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请分析以下错题：\n\n");
        prompt.append("用户答案：").append(request.getUserAnswer()).append("\n");
        prompt.append("正确答案：").append(request.getCorrectAnswer()).append("\n\n");
        prompt.append("请分析：\n");
        prompt.append("1. 错误原因是什么？\n");
        prompt.append("2. 这个知识点需要如何掌握？\n");
        prompt.append("3. 给出学习建议。");

        return prompt.toString();
    }

    public Map<String, Object> getPersonalizedRecommendation(Long userId) {
        String prompt = buildRecommendPrompt(userId);

        try {
            String response = callDeepSeekApi(prompt);
            return parseRecommendResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("AI推荐失败: " + e.getMessage());
        }
    }

    private String buildRecommendPrompt(Long userId) {
        return "请根据用户ID为" + userId + "的学习情况，推荐合适的学习内容。";
    }

    private Map<String, Object> parseRecommendResponse(String response) {
        Map<String, Object> result = new HashMap<>();
        result.put("recommendation", response);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    public String autoGrade(Long questionId, String userAnswer, String correctAnswer, Integer questionType) {
        String prompt = buildGradePrompt(questionType, userAnswer, correctAnswer);

        try {
            String response = callDeepSeekApi(prompt);
            return response;
        } catch (Exception e) {
            return "grading_failed";
        }
    }

    private String buildGradePrompt(Integer questionType, String userAnswer, String correctAnswer) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请判断以下答案是否正确：\n");
        prompt.append("题目类型：").append(getQuestionTypeText(questionType)).append("\n");
        prompt.append("用户答案：").append(userAnswer).append("\n");
        prompt.append("正确答案：").append(correctAnswer).append("\n");
        prompt.append("\n如果是简答题，请给出评分(0-100)和简要评语。");
        return prompt.toString();
    }

    private String getQuestionTypeText(Integer type) {
        switch (type) {
            case 1: return "单选题";
            case 2: return "多选题";
            case 3: return "判断题";
            case 4: return "简答题";
            default: return "未知类型";
        }
    }

    private String callDeepSeekApi(String prompt) throws Exception {
        MediaType mediaType = MediaType.parse("application/json");

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("messages", new JSONArray().fluentAdd(new JSONObject()
                .put("role", "user")
                .put("content", prompt)));

        RequestBody body = RequestBody.create(requestBody.toJSONString(), mediaType);

        Request request = new Request.Builder()
                .url(apiUrl + "/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("API调用失败: " + response.code());
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = JSON.parseObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");

            if (choices != null && choices.size() > 0) {
                return choices.getJSONObject(0).getJSONObject("message").getString("content");
            }

            throw new RuntimeException("API响应格式错误");
        }
    }
}
