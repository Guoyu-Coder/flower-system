package com.flowershop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@ConditionalOnClass(StringRedisTemplate.class)
public class RedisChatService {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CHAT_SESSION_PREFIX = "chat:session:";
    private static final String GUEST_CHAT_PREFIX = "chat:guest:";
    private static final long SESSION_TTL = 24;
    private static final String USER_CHAT_KEY = "chat:user:";
    private static final String CONTEXT_KEY = "chat:context:"; // 新增：上下文存储key

    public void saveUserMessage(Long userId, String role, String content) {
        if (redisTemplate == null) {
            log.warn("Redis is not available, skipping saveUserMessage");
            return;
        }
        String key = USER_CHAT_KEY + userId;
        ChatMessage msg = new ChatMessage(role, content, System.currentTimeMillis());
        try {
            String json = objectMapper.writeValueAsString(msg);
            redisTemplate.opsForList().rightPush(key, json);
            Long size = redisTemplate.opsForList().size(key);
            if (size != null && size > 50) {
                redisTemplate.opsForList().leftPop(key);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化消息失败", e);
        }
    }

    public List<ChatMessage> getUserMessages(Long userId) {
        if (redisTemplate == null) {
            log.warn("Redis is not available, returning empty list");
            return new ArrayList<>();
        }
        String key = USER_CHAT_KEY + userId;
        List<String> jsons = redisTemplate.opsForList().range(key, 0, -1);
        return parseMessages(jsons);
    }

    public void saveGuestMessage(String sessionId, String role, String content) {
        if (redisTemplate == null) {
            log.warn("Redis is not available, skipping saveGuestMessage");
            return;
        }
        String key = GUEST_CHAT_PREFIX + sessionId;
        ChatMessage msg = new ChatMessage(role, content, System.currentTimeMillis());
        try {
            String json = objectMapper.writeValueAsString(msg);
            redisTemplate.opsForList().rightPush(key, json);
            redisTemplate.expire(key, SESSION_TTL, TimeUnit.HOURS);
            Long size = redisTemplate.opsForList().size(key);
            if (size != null && size > 30) {
                redisTemplate.opsForList().leftPop(key);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化消息失败", e);
        }
    }

    public List<ChatMessage> getGuestMessages(String sessionId) {
        if (redisTemplate == null) {
            log.warn("Redis is not available, returning empty list");
            return new ArrayList<>();
        }
        String key = GUEST_CHAT_PREFIX + sessionId;
        List<String> jsons = redisTemplate.opsForList().range(key, 0, -1);
        return parseMessages(jsons);
    }

    public void clearGuestMessages(String sessionId) {
        if (redisTemplate == null) {
            log.warn("Redis is not available, skipping clearGuestMessages");
            return;
        }
        String key = GUEST_CHAT_PREFIX + sessionId;
        redisTemplate.delete(key);
    }

    private List<ChatMessage> parseMessages(List<String> jsons) {
        List<ChatMessage> messages = new ArrayList<>();
        if (jsons == null) return messages;
        for (String json : jsons) {
            try {
                ChatMessage msg = objectMapper.readValue(json, ChatMessage.class);
                messages.add(msg);
            } catch (JsonProcessingException e) {
            }
        }
        return messages;
    }

    public List<Map<String, String>> buildContext(Long userId, String sessionId) {
        List<ChatMessage> history;
        if (userId != null) {
            history = getUserMessages(userId);
        } else {
            history = getGuestMessages(sessionId);
        }

        List<Map<String, String>> context = new ArrayList<>();
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", SYSTEM_PROMPT);
        context.add(systemMsg);
        
        for (ChatMessage msg : history) {
            Map<String, String> map = new HashMap<>();
            map.put("role", msg.getRole());
            map.put("content", msg.getContent());
            context.add(map);
        }
        return context;
    }

    public void clearUserMessages(Long userId) {
        if (redisTemplate == null) {
            log.warn("Redis is not available, skipping clearUserMessages");
            return;
        }
        redisTemplate.delete(USER_CHAT_KEY + userId);
    }

    public static class ChatMessage {
        private String role;
        private String content;
        private long timestamp;

        public ChatMessage() {}

        public ChatMessage(String role, String content, long timestamp) {
            this.role = role;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    private static final String SYSTEM_PROMPT = "你是花语轩鲜花商城的专业AI导购助手，名为'花语助手'。\n\n" +
            "【商城商品列表】\n" +
            "---\n" +
            "红玫瑰系列：\n" +
            "- 红玫瑰11朵花束 | 花语：一心一意的爱 | 价格：¥128\n" +
            "- 红玫瑰33朵花束 | 花语：三生三世的爱恋 | 价格：¥268\n" +
            "- 红玫瑰99朵花束 | 花语：天长地久的承诺 | 价格：¥368\n" +
            "- 情人节礼盒 | 花语：浪漫永恒 | 价格：¥299\n" +
            "\n" +
            "粉玫瑰系列：\n" +
            "- 粉玫瑰11朵花束 | 花语：初恋的甜蜜 | 价格：¥138\n" +
            "- 粉玫瑰33朵花束 | 花语：浪漫的爱 | 价格：¥278\n" +
            "\n" +
            "白玫瑰系列：\n" +
            "- 白玫瑰19朵花束 | 花语：纯洁的爱 | 价格：¥168\n" +
            "\n" +
            "香槟玫瑰系列：\n" +
            "- 香槟玫瑰52朵花束 | 花语：优雅的爱 | 价格：¥398\n" +
            "\n" +
            "百合系列：\n" +
            "- 香水百合6朵花束 | 花语：纯洁高贵 | 价格：¥158\n" +
            "- 白百合6朵花束 | 花语：百年好合 | 价格：¥148\n" +
            "- 多头百合礼盒 | 花语：幸福美满 | 价格：¥198\n" +
            "\n" +
            "康乃馨系列：\n" +
            "- 康乃馨花篮 | 花语：感恩母爱 | 价格：¥128\n" +
            "- 红色康乃馨花束 | 花语：热情的爱 | 价格：¥98\n" +
            "- 母亲节礼盒 | 花语：深深祝福 | 价格：¥188\n" +
            "\n" +
            "向日葵系列：\n" +
            "- 向日葵5朵花束 | 花语：阳光活力 | 价格：¥88\n" +
            "- 向日葵单支 | 花语：忠诚爱慕 | 价格：¥25\n" +
            "- 向日葵混搭花束 | 花语：积极向上 | 价格：¥158\n" +
            "\n" +
            "郁金香系列：\n" +
            "- 荷兰郁金香礼盒 | 花语：完美的爱情 | 价格：¥228\n" +
            "- 紫色郁金香花束 | 花语：高贵优雅 | 价格：¥188\n" +
            "- 混搭郁金香 | 花语：无尽的爱 | 价格：¥168\n" +
            "\n" +
            "【你的职责】\n" +
            "1. 必须根据上方商品列表推荐鲜花，禁止推荐列表外的商品\n" +
            "2. 根据用户需求推荐合适的鲜花（考虑送礼对象、预算、节日、场合）\n" +
            "3. 解答各种鲜花的花语、寓意\n" +
            "4. 回答关于订单物流、售后等客服问题\n" +
            "5. 告知送花礼仪和禁忌\n" +
            "\n" +
            "【回答要求】\n" +
            "- 语气温柔亲切，使用粉色主题相关表情 🌸🌷💐\n" +
            "- 推荐鲜花时必须从商品列表中选择，给出具体花名、花语、价格\n" +
            "- 推荐格式：花名 | 花语 | 价格 | 推荐理由\n" +
            "- 如果用户想购买，请在回复末尾加上【一键加购】关键词\n" +
            "- 对于订单查询问题，要求用户提供订单号或手机号\n" +
            "- 如果不确定信息，诚实告知用户\n" +
            "- 禁止推荐商城商品列表以外的任何鲜花\n" +
            "- 所有推荐必须来自花语轩商城的商品";
    
    // ==================== 上下文管理方法 ====================
    
    /**
     * 保存对话上下文（推荐的商品列表）
     */
    public void saveContext(Long userId, String sessionId, List<Long> productIds) {
        if (redisTemplate == null) {
            log.warn("Redis is not available, skipping saveContext");
            return;
        }
        String key = getContextKey(userId, sessionId);
        try {
            String json = objectMapper.writeValueAsString(productIds);
            redisTemplate.opsForValue().set(key, json, SESSION_TTL, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("保存上下文失败", e);
        }
    }
    
    /**
     * 获取对话上下文（推荐的商品列表）
     */
    public List<Long> getContext(Long userId, String sessionId) {
        if (redisTemplate == null) {
            log.warn("Redis is not available, returning empty list");
            return new ArrayList<>();
        }
        String key = getContextKey(userId, sessionId);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, List.class);
        } catch (Exception e) {
            log.error("解析上下文失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 清除对话上下文
     */
    public void clearContext(Long userId, String sessionId) {
        if (redisTemplate == null) {
            log.warn("Redis is not available, skipping clearContext");
            return;
        }
        String key = getContextKey(userId, sessionId);
        redisTemplate.delete(key);
    }
    
    private String getContextKey(Long userId, String sessionId) {
        if (userId != null) {
            return CONTEXT_KEY + "user:" + userId;
        }
        return CONTEXT_KEY + "guest:" + sessionId;
    }
}
