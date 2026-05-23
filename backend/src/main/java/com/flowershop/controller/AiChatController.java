package com.flowershop.controller;

import com.flowershop.common.Result;
import com.flowershop.entity.Cart;
import com.flowershop.entity.Product;
import com.flowershop.service.CartService;
import com.flowershop.service.DeepSeekService;
import com.flowershop.service.ProductService;
import com.flowershop.service.RedisChatService;
import com.flowershop.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Autowired
    private DeepSeekService deepSeekService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired(required = false)
    private RedisChatService redisChatService;

    @PostMapping("/chat")
    public Result chat(@RequestBody Map<String, Object> params,
                       @RequestHeader(value = "Authorization", required = false) String token) {
        String message = (String) params.get("message");
        String sessionId = (String) params.get("sessionId");

        if (message == null || message.trim().isEmpty()) {
            return Result.error("请输入消息");
        }

        Long userId = null;
        if (token != null && !token.trim().isEmpty()) {
            try {
                String actualToken = token;
                if (token.startsWith("Bearer ")) {
                    actualToken = token.substring(7);
                }
                actualToken = actualToken.trim();
                if (!actualToken.isEmpty()) {
                    userId = JwtUtils.getUserId(actualToken);
                }
            } catch (Exception e) {
                log.warn("Token解析失败: {}", e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> addedProducts = new ArrayList<>();
        List<Map<String, Object>> bundledProducts = new ArrayList<>();
        
        // 1️⃣ 检查多轮对话：解析"第n个"或"就要第2个"等指令
        if (isMultiSelectInstruction(message)) {
            int index = parseSelectIndex(message);
            if (index >= 0 && redisChatService != null) {
                List<Long> contextProductIds = redisChatService.getContext(userId, sessionId);
                if (!contextProductIds.isEmpty() && index < contextProductIds.size()) {
                    Product selectedProduct = productService.getById(contextProductIds.get(index));
                    if (selectedProduct != null && userId != null) {
                        try {
                            cartService.addCart(userId, selectedProduct.getId(), 1);
                            Map<String, Object> p = new HashMap<>();
                            p.put("id", selectedProduct.getId());
                            p.put("name", selectedProduct.getName());
                            p.put("price", selectedProduct.getDiscountPrice() != null ? selectedProduct.getDiscountPrice() : selectedProduct.getPrice());
                            addedProducts.add(p);
                            saveMessage(userId, sessionId, "user", message);
                            String successReply = "好的！已为您将「" + selectedProduct.getName() + "」加入购物车了🛒\n" +
                                    "您可以点击购物车查看并结算哦~";
                            saveMessage(userId, sessionId, "assistant", successReply);
                            result.put("reply", successReply);
                            result.put("hasRecommend", false);
                            result.put("addToCart", addedProducts);
                            return Result.success(result);
                        } catch (Exception e) {
                            log.error("加购物车失败: {}", e.getMessage(), e);
                        }
                    }
                }
            }
        }

        // 2️⃣ 检查是否要加购物车
        if (userId != null && isAddToCartIntent(message)) {
            Product product = findProductFromMessage(message);
            if (product != null) {
                try {
                    cartService.addCart(userId, product.getId(), 1);
                    Map<String, Object> p = new HashMap<>();
                    p.put("id", product.getId());
                    p.put("name", product.getName());
                    p.put("price", product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice());
                    p.put("coverImage", product.getCoverImage());
                    p.put("flowerLanguage", product.getFlowerLanguage());
                    addedProducts.add(p);
                    saveMessage(userId, sessionId, "user", message);
                    String successReply = "好的！已为您将「" + product.getName() + "」加入购物车了🛒\n" +
                            "您可以点击购物车查看并结算哦~";
                    saveMessage(userId, sessionId, "assistant", successReply);
                    
                    // 🔍 获取搭配推荐（智能购物篮）
                    List<Product> bundled = productService.getBundledRecommendations(product.getId());
                    for (Product bp : bundled) {
                        Map<String, Object> bpMap = new HashMap<>();
                        bpMap.put("id", bp.getId());
                        bpMap.put("name", bp.getName());
                        bpMap.put("price", bp.getDiscountPrice() != null ? bp.getDiscountPrice() : bp.getPrice());
                        bpMap.put("originalPrice", bp.getPrice());
                        bpMap.put("coverImage", bp.getCoverImage());
                        bpMap.put("flowerLanguage", bp.getFlowerLanguage());
                        bpMap.put("stock", bp.getStock());
                        bundledProducts.add(bpMap);
                    }
                    
                    result.put("reply", successReply);
                    result.put("hasRecommend", !bundledProducts.isEmpty());
                    result.put("addToCart", addedProducts);
                    result.put("bundledRecommendations", bundledProducts);
                    return Result.success(result);
                } catch (Exception e) {
                    log.error("加购物车失败: {}", e.getMessage(), e);
                }
            }
        }

        List<Map<String, String>> context = buildContext(userId, sessionId, message);

        String reply;
        List<Product> recommendedProducts = new ArrayList<>();
        try {
            if (userId != null) {
                reply = deepSeekService.chat(context, userId);
            } else {
                reply = deepSeekService.chat(context);
            }
            // 提取推荐商品
            recommendedProducts = deepSeekService.extractRecommendedProducts(reply);
        } catch (Exception e) {
            log.error("AI聊天异常: {}", e.getMessage(), e);
            reply = "抱歉，我暂时无法回答，请稍后再试。🌸";
        }

        saveMessage(userId, sessionId, "assistant", reply);

        // 转换推荐商品为Map列表
        List<Map<String, Object>> recommendedList = new ArrayList<>();
        List<Long> productIdsForContext = new ArrayList<>();
        for (Product p : recommendedProducts) {
            Map<String, Object> pMap = new HashMap<>();
            pMap.put("id", p.getId());
            pMap.put("name", p.getName());
            pMap.put("price", p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice());
            pMap.put("originalPrice", p.getPrice());
            pMap.put("coverImage", p.getCoverImage());
            pMap.put("flowerLanguage", p.getFlowerLanguage());
            pMap.put("stock", p.getStock());
            pMap.put("sales", p.getSales());
            recommendedList.add(pMap);
            productIdsForContext.add(p.getId());
        }

        // 📝 保存上下文（用于多轮对话
        if (redisChatService != null && !productIdsForContext.isEmpty()) {
            redisChatService.saveContext(userId, sessionId, productIdsForContext);
        }

        result.put("reply", reply);
        result.put("hasRecommend", !recommendedList.isEmpty() ? false : reply.contains("一键加购"));
        result.put("recommendedProducts", recommendedList);
        
        return Result.success(result);
    }

    @PostMapping("/quick-add-cart")
    public Result quickAddToCart(@RequestBody Map<String, Object> params,
                                 @RequestHeader(value = "Authorization", required = false) String token) {
        Long productId = ((Number) params.get("productId")).longValue();
        Integer quantity = params.get("quantity") != null ? ((Number) params.get("quantity")).intValue() : 1;

        Long userId = null;
        if (token != null && !token.trim().isEmpty()) {
            try {
                String actualToken = token;
                if (token.startsWith("Bearer ")) {
                    actualToken = token.substring(7);
                }
                actualToken = actualToken.trim();
                if (!actualToken.isEmpty()) {
                    userId = JwtUtils.getUserId(actualToken);
                }
            } catch (Exception e) {
                log.warn("Token解析失败: {}", e.getMessage());
            }
        }

        if (userId == null) {
            return Result.error("请先登录");
        }

        try {
            cartService.addCart(userId, productId, quantity);
            
            Map<String, Object> result = new HashMap<>();
            
            // 🔍 获取搭配推荐
            List<Map<String, Object>> bundled = new ArrayList<>();
            List<Product> bundledList = productService.getBundledRecommendations(productId);
            for (Product bp : bundledList) {
                Map<String, Object> bpMap = new HashMap<>();
                bpMap.put("id", bp.getId());
                bpMap.put("name", bp.getName());
                bpMap.put("price", bp.getDiscountPrice() != null ? bp.getDiscountPrice() : bp.getPrice());
                bpMap.put("originalPrice", bp.getPrice());
                bpMap.put("coverImage", bp.getCoverImage());
                bpMap.put("flowerLanguage", bp.getFlowerLanguage());
                bpMap.put("stock", bp.getStock());
                bundled.add(bpMap);
            }
            
            result.put("bundledRecommendations", bundled);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("加购物车失败: {}", e.getMessage(), e);
            return Result.error("加入购物车失败");
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 是否是多轮选择指令（"第2个"、"就要第1个"等
     */
    private boolean isMultiSelectInstruction(String msg) {
        String lowerMsg = msg.toLowerCase();
        return lowerMsg.contains("第") && (lowerMsg.contains("个") || lowerMsg.contains("个吧") || lowerMsg.contains("就"));
    }

    /**
     * 解析选择的索引（0开始）
     */
    private int parseSelectIndex(String msg) {
        Pattern pattern = Pattern.compile("第(\\d+)");
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) - 1;
        }
        return -1;
    }

    private boolean isAddToCartIntent(String message) {
        String lowerMsg = message.toLowerCase();
        return lowerMsg.contains("加入购物车") || 
               lowerMsg.contains("加购物车") || 
               lowerMsg.contains("放入购物车") || 
               lowerMsg.contains("买这个") || 
               lowerMsg.contains("我要这个");
    }

    private Product findProductFromMessage(String message) {
        List<Product> products = productService.list();
        for (Product p : products) {
            if (message.contains(p.getName())) {
                return p;
            }
        }
        for (Product p : products) {
            String[] keywords = p.getName().split("[，,。.、 ]");
            for (String kw : keywords) {
                if (!kw.trim().isEmpty() && message.contains(kw.trim())) {
                    return p;
                }
            }
        }
        return null;
    }

    private String buildProductList() {
        List<Product> products = productService.list();
        if (products.isEmpty()) {
            return "暂无商品";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            sb.append(i + 1).append(". ").append(p.getName());
            sb.append(" | ID:").append(p.getId());
            sb.append(" | 花语：").append(p.getFlowerLanguage() != null ? p.getFlowerLanguage() : "暂无花语");
            sb.append(" | 价格：¥").append(p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice());
            sb.append(" | 库存：").append(p.getStock() != null ? p.getStock() : 0);
            sb.append("\n");
        }
        return sb.toString();
    }

    private List<Map<String, String>> buildContext(Long userId, String sessionId, String currentMessage) {
        List<Map<String, String>> context = new ArrayList<>();
        
        Map<String, String> systemMessage = new HashMap<>();
        String productList = buildProductList();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是花语轩鲜花商城的专业AI导购助手，你的名字叫花语助手。你只能根据以下商品列表推荐鲜花，不能说通用答案或推荐商城以外的商品。\n\n【商城商品列表】\n" + productList + "\n【回答要求】\n1. 必须从商品列表中选择推荐，不能推荐列表外的商品\n2. 回答要温柔亲切，使用鲜花相关的表情\n3. 当用户询问节日送什么花时，要给出具体的商品推荐，并在商品名称后标注 ID:商品ID\n4. 如果用户想购买，请在回复末尾加上【一键加购】关键词\n5. 如果用户询问订单相关问题，要求用户提供订单号或手机号\n6. 如果不确定信息，诚实告知用户\n7. 禁止推荐商城商品列表以外的任何鲜花\n8. 所有推荐必须来自花语轩商城的商品");
        context.add(systemMessage);

        if (redisChatService != null) {
            try {
                List<RedisChatService.ChatMessage> history;
                if (userId != null) {
                    history = redisChatService.getUserMessages(userId);
                } else if (sessionId != null) {
                    history = redisChatService.getGuestMessages(sessionId);
                } else {
                    history = new ArrayList<>();
                }
                
                int startIndex = Math.max(0, history.size() - 10);
                for (int i = startIndex; i < history.size(); i++) {
                    RedisChatService.ChatMessage msg = history.get(i);
                    Map<String, String> msgMap = new HashMap<>();
                    msgMap.put("role", msg.getRole());
                    msgMap.put("content", msg.getContent());
                    context.add(msgMap);
                }
            } catch (Exception e) {
                log.warn("获取对话历史失败，使用当前消息: {}", e.getMessage());
            }
        }

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", currentMessage);
        context.add(userMessage);

        saveMessage(userId, sessionId, "user", currentMessage);

        return context;
    }

    private void saveMessage(Long userId, String sessionId, String role, String content) {
        if (redisChatService != null) {
            try {
                if (userId != null) {
                    redisChatService.saveUserMessage(userId, role, content);
                } else if (sessionId != null) {
                    redisChatService.saveGuestMessage(sessionId, role, content);
                }
            } catch (Exception e) {
                log.warn("保存消息失败: {}", e.getMessage());
            }
        }
    }

    @GetMapping("/history")
    public Result history(@RequestHeader(value = "Authorization", required = false) String token,
                         @RequestParam(required = false) String sessionId) {
        if (redisChatService == null) {
            return Result.success(new ArrayList<>());
        }

        Long userId = null;
        if (token != null && !token.trim().isEmpty()) {
            try {
                String actualToken = token;
                if (token.startsWith("Bearer ")) {
                    actualToken = token.substring(7);
                }
                actualToken = actualToken.trim();
                if (!actualToken.isEmpty()) {
                    userId = JwtUtils.getUserId(actualToken);
                }
            } catch (Exception e) {
                log.warn("Token解析失败: {}", e.getMessage());
            }
        }

        try {
            if (userId != null) {
                return Result.success(redisChatService.getUserMessages(userId));
            } else if (sessionId != null && !sessionId.trim().isEmpty()) {
                return Result.success(redisChatService.getGuestMessages(sessionId));
            }
        } catch (Exception e) {
            log.warn("获取历史记录失败: {}", e.getMessage());
        }
        return Result.success(new ArrayList<>());
    }

    @DeleteMapping("/history")
    public Result clearHistory(@RequestHeader(value = "Authorization", required = false) String token,
                              @RequestParam(required = false) String sessionId) {
        if (redisChatService == null) {
            return Result.success("对话历史已清空");
        }

        Long userId = null;
        if (token != null && !token.trim().isEmpty()) {
            try {
                String actualToken = token;
                if (token.startsWith("Bearer ")) {
                    actualToken = token.substring(7);
                }
                actualToken = actualToken.trim();
                if (!actualToken.isEmpty()) {
                    userId = JwtUtils.getUserId(actualToken);
                }
            } catch (Exception e) {
                return Result.error("无效的token");
            }
        }

        try {
            if (userId != null) {
                redisChatService.clearUserMessages(userId);
                redisChatService.clearContext(userId, sessionId);
                return Result.success("对话历史已清空");
            } else if (sessionId != null && !sessionId.trim().isEmpty()) {
                redisChatService.clearGuestMessages(sessionId);
                redisChatService.clearContext(userId, sessionId);
                return Result.success("对话历史已清空");
            }
        } catch (Exception e) {
            log.warn("清空历史记录失败: {}", e.getMessage());
        }
        return Result.error("请提供用户ID或会话ID");
    }

    @GetMapping("/recommend")
    public Result recommend(@RequestParam String occasion) {
        Map<String, Object> result = new HashMap<>();
        result.put("occasion", occasion);
        result.put("message", "功能开发中...");
        return Result.success(result);
    }

    @GetMapping("/banners")
    public Result getAiBanners() {
        try {
            List<Map<String, Object>> banners = deepSeekService.generateBanners();
            return Result.success(banners);
        } catch (Exception e) {
            log.error("获取AI轮播图失败: {}", e.getMessage(), e);
            return Result.error("获取轮播图失败");
        }
    }
}
