package com.flowershop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowershop.entity.FlowerLanguage;
import com.flowershop.entity.Order;
import com.flowershop.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.regex.*;

@Slf4j
@Service
public class DeepSeekService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @Autowired
    private FlowerLanguageService flowerLanguageService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeepSeekService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(Proxy.NO_PROXY);
        this.restTemplate = new RestTemplate(factory);
    }

    public String chat(List<Map<String, String>> messages, Long userId) {
        try {
            String userMessage = "";
            for (int i = messages.size() - 1; i >= 0; i--) {
                Map<String, String> msg = messages.get(i);
                if ("user".equals(msg.get("role"))) {
                    userMessage = msg.get("content");
                    break;
                }
            }

            String orderInfo = "";
            if (userId != null) {
                orderInfo = extractOrderInfo(userMessage, userId);
            }

            List<Map<String, String>> enhancedMessages = new ArrayList<>(messages);
            if (!orderInfo.isEmpty()) {
                Map<String, String> systemInstruction = new HashMap<>();
                systemInstruction.put("role", "system");
                systemInstruction.put("content", "\n\n【用户订单信息】\n" + orderInfo + "\n\n请根据以上订单信息回答用户的相关问题。");
                enhancedMessages.add(enhancedMessages.size() - 1, systemInstruction);
            }

            try {
                return callDeepSeekApi(enhancedMessages);
            } catch (Exception e) {
                log.warn("DeepSeek API调用失败，使用本地回复: {}", e.getMessage());
                return getLocalReply(messages);
            }
        } catch (Exception e) {
            log.error("AI聊天服务异常: {}", e.getMessage(), e);
            return getLocalReply(messages);
        }
    }

    public String chat(List<Map<String, String>> messages) {
        return chat(messages, null);
    }

    public List<Map<String, Object>> generateBanners() {
        List<Map<String, Object>> banners = new ArrayList<>();
        
        try {
            List<Product> hotProducts = productService.getHotProducts(3);
            String productInfo = "";
            for (Product p : hotProducts) {
                productInfo += p.getName() + " (¥" + (p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice()) + "), ";
            }
            if (!productInfo.isEmpty()) {
                productInfo = productInfo.substring(0, productInfo.length() - 2);
            }

            String prompt = "作为花语轩鲜花商城的AI助手，请为我生成3个轮播横幅内容。每个横幅需要包含：\n" +
                    "1. title: 吸引人的主标题（15字以内）\n" +
                    "2. subtitle: 副标题（20字以内）\n" +
                    "3. bg: 渐变色背景（格式：linear-gradient(135deg, #颜色1, #颜色2)）\n" +
                    "4. emoji: 相关的emoji表情\n" +
                    "\n当前热门商品：" + productInfo + "\n" +
                    "\n请直接返回JSON数组格式，不要有其他文字。示例格式：\n" +
                    "[{\"title\":\"标题1\",\"subtitle\":\"副标题1\",\"bg\":\"linear-gradient(135deg, #ff9a9e, #fad0c4)\",\"emoji\":\"🌹\"},{\"title\":\"标题2\",\"subtitle\":\"副标题2\",\"bg\":\"linear-gradient(135deg, #a18cd1, #fbc2eb)\",\"emoji\":\"🎁\"},{\"title\":\"标题3\",\"subtitle\":\"副标题3\",\"bg\":\"linear-gradient(135deg, #fccb90, #d57eeb)\",\"emoji\":\"💐\"}]";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");
            
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是一个专业的营销文案生成助手，擅长生成吸引人的促销横幅内容。请严格按照要求的JSON格式输出，不要添加任何额外文字。");
            messages.add(systemMsg);
            
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            String json = objectMapper.writeValueAsString(requestBody);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);

            json = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            
            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    
                    try {
                        banners = objectMapper.readValue(content, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});
                    } catch (Exception e) {
                        log.warn("解析AI返回的JSON失败，使用默认横幅: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("生成AI轮播图失败，使用默认横幅: {}", e.getMessage());
        }

        if (banners.isEmpty()) {
            banners.add(Map.of("title", "🌹 每一朵花，都是一句情话", "subtitle", "甄选全球优质鲜花，用心传递温暖", "bg", "linear-gradient(135deg, #ff9a9e, #fad0c4)", "emoji", "🌹"));
            banners.add(Map.of("title", "🎁 节日送礼首选花语轩", "subtitle", "专业花艺师搭配，精美礼盒包装", "bg", "linear-gradient(135deg, #a18cd1, #fbc2eb)", "emoji", "🎁"));
            banners.add(Map.of("title", "💐 新品鲜花限时特惠", "subtitle", "全场满199包邮，买二送一", "bg", "linear-gradient(135deg, #fccb90, #d57eeb)", "emoji", "💐"));
        }
        
        return banners;
    }

    public List<Product> extractRecommendedProducts(String reply) {
        List<Product> recommended = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile("ID:(\\d+)");
            Matcher matcher = pattern.matcher(reply);
            Set<Long> foundIds = new HashSet<>();
            while (matcher.find()) {
                Long id = Long.parseLong(matcher.group(1));
                if (!foundIds.contains(id)) {
                    foundIds.add(id);
                    Product p = productService.getById(id);
                    if (p != null) {
                        recommended.add(p);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("提取推荐商品失败: {}", e.getMessage());
        }
        return recommended;
    }

    private String callDeepSeekApi(List<Map<String, String>> messages) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.8);
        requestBody.put("max_tokens", 2000);

        String json = objectMapper.writeValueAsString(requestBody);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl, HttpMethod.POST, entity, Map.class);

        if (response.getBody() != null) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        }
        return null;
    }

    private String extractOrderInfo(String userMessage, Long userId) {
        StringBuilder info = new StringBuilder();

        if (userMessage.contains("订单") || userMessage.contains("查询") ||
            userMessage.contains("物流") || userMessage.contains("配送") ||
            userMessage.contains("什么时候") || userMessage.contains("到了吗")) {

            try {
                List<Order> recentOrders = orderService.lambdaQuery()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreatedAt)
                        .last("LIMIT 5")
                        .list();

                if (!recentOrders.isEmpty()) {
                    info.append("您的最近订单：\n");
                    for (Order order : recentOrders) {
                        info.append("- 订单号：").append(order.getOrderNo()).append("\n");
                        info.append("  状态：").append(getStatusText(order.getStatus())).append("\n");
                        info.append("  金额：￥").append(order.getPayAmount()).append("\n");
                        info.append("  时间：").append(order.getCreatedAt()).append("\n");

                        if (order.getDeliveryTime() != null) {
                            info.append("  配送时间：").append(order.getDeliveryTime()).append("\n");
                        }
                        if (order.getReceiverAddress() != null) {
                            info.append("  配送地址：").append(order.getReceiverAddress()).append("\n");
                        }
                        info.append("\n");
                    }
                } else {
                    info.append("您目前没有订单记录。\n");
                }
            } catch (Exception e) {
                log.warn("获取订单信息失败: {}", e.getMessage());
            }
        }

        return info.toString();
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待支付";
            case 1: return "待发货";
            case 2: return "配送中";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }

    private String getLocalReply(List<Map<String, String>> messages) {
        String userMessage = "";
        for (int i = messages.size() - 1; i >= 0; i--) {
            Map<String, String> msg = messages.get(i);
            if ("user".equals(msg.get("role"))) {
                userMessage = msg.get("content");
                break;
            }
        }

        if (userMessage.isEmpty()) {
            return "你好呀！我是花语助手🌸，有什么可以帮你的吗？我可以帮你推荐鲜花、查询花语、解答问题哦~";
        }

        String reply = recommendProducts(userMessage);
        if (reply != null) {
            return reply;
        }

        String knowledgeReply = searchFlowerKnowledge(userMessage);
        if (knowledgeReply != null) {
            return knowledgeReply;
        }

        return "关于这个问题，让我想想...🌸 你可以试试问我：\n" +
                "1. 推荐送女朋友什么花？\n" +
                "2. 玫瑰的花语是什么？\n" +
                "3. 有什么适合母亲节的花？\n" +
                "4. 有没有200元以内的花束推荐？";
    }

    private String searchFlowerKnowledge(String query) {
        try {
            List<FlowerLanguage> results = flowerLanguageService.searchByKeyword(query);
            if (!results.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("我找到了一些相关的花语知识，分享给你哦~ 🌸\n\n");
                int limit = Math.min(results.size(), 5);
                for (int i = 0; i < limit; i++) {
                    FlowerLanguage fl = results.get(i);
                    sb.append(i + 1).append(". ").append(fl.getName()).append("\n");
                    sb.append("   ✿ 花语：").append(fl.getFlowerLanguage()).append("\n");
                    if (fl.getMeaning() != null && !fl.getMeaning().isEmpty()) {
                        sb.append("   ✿ 寓意：").append(fl.getMeaning()).append("\n");
                    }
                    if (fl.getOccasion() != null && !fl.getOccasion().isEmpty()) {
                        sb.append("   ✿ 适用场景：").append(fl.getOccasion()).append("\n");
                    }
                    sb.append("\n");
                }
                sb.append("想要了解更多，或者需要我帮你把心仪的花花加入购物车吗？💐");
                return sb.toString();
            }
        } catch (Exception e) {
            log.warn("搜索花语知识失败: {}", e.getMessage());
        }
        return null;
    }

    private String recommendProducts(String query) {
        try {
            List<Product> products;
            String[] occasionKeywords = {"生日", "母亲", "父亲", "情人节", "七夕", "纪念日", "毕业", "探病", "道歉", "求婚", "结婚", "节日", "母亲节", "教师", "送女", "送男", "送朋友"};

            for (String kw : occasionKeywords) {
                if (query.contains(kw)) {
                    String occasion = null;
                    if (query.contains("母亲") || query.contains("母亲节")) occasion = "母亲节";
                    else if (query.contains("父亲")) occasion = "父亲节";
                    else if (query.contains("情人节") || query.contains("七夕")) occasion = "情人节";
                    else if (query.contains("生日")) occasion = "生日";
                    else if (query.contains("求婚")) occasion = "求婚";
                    else if (query.contains("结婚")) occasion = "结婚";
                    else if (query.contains("道歉")) occasion = "道歉";
                    else if (query.contains("探病")) occasion = "探病";
                    else if (query.contains("毕业")) occasion = "毕业";
                    else if (query.contains("纪念日")) occasion = "纪念日";

                    if (occasion != null) {
                        products = productService.getRecommendByOccasion(occasion, 4);
                        if (!products.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("为你推荐适合").append(occasion).append("的鲜花~ 💐\n\n");
                            for (Product p : products) {
                                sb.append("• ").append(p.getName());
                                sb.append(" | ID:").append(p.getId());
                                sb.append(" | ￥").append(p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice());
                                sb.append("\n");
                            }
                            sb.append("\n回复【一键加购】可以帮你加入购物车哦~ 🌷");
                            return sb.toString();
                        }
                    }
                }
            }

            products = productService.getHotProducts(5);
            if (!products.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("为你推荐几款热销鲜花~ 🌸\n\n");
                for (Product p : products) {
                    sb.append("• ").append(p.getName());
                    sb.append(" | ID:").append(p.getId());
                    sb.append(" | ￥").append(p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice());
                    if (p.getSales() != null) sb.append(" (已售").append(p.getSales()).append("件)");
                    sb.append("\n");
                }
                sb.append("\n回复【一键加购】可以帮你加入购物车哦~ 告诉我你想要哪一款？💐");
                return sb.toString();
            }
        } catch (Exception e) {
            log.warn("推荐商品失败: {}", e.getMessage());
        }

        return null;
    }
}
