package com.flowershop.controller;

import com.flowershop.common.Result;
import com.flowershop.entity.Order;
import com.flowershop.service.OrderService;
import com.flowershop.service.RedisChatService;
import com.flowershop.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired(required = false)
    private RedisChatService redisChatService;

    @PostMapping("/create")
    public Result create(@RequestHeader(value = "Authorization", required = false) String token,
                       @RequestBody Map<String, Object> params) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        
        log.info("创建订单 - userId: {}, params: {}", userId, params);

        Long addressId = params.get("addressId") != null ? Long.valueOf(params.get("addressId").toString()) : null;
        String remark = (String) params.get("remark");
        @SuppressWarnings("unchecked")
        List<Long> cartIds = (List<Long>) params.get("cartIds");
        
        try {
            Order order = orderService.createOrder(userId, addressId, remark, cartIds);
            log.info("订单创建成功 - orderId: {}, orderNo: {}", order.getId(), order.getOrderNo());
            return Result.success(order);
        } catch (RuntimeException e) {
            log.error("订单创建失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/create-direct")
    public Result createDirect(@RequestHeader(value = "Authorization", required = false) String token,
                            @RequestBody Map<String, Object> params) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        
        log.info("立即购买 - userId: {}, params: {}", userId, params);

        Long addressId = params.get("addressId") != null ? Long.valueOf(params.get("addressId").toString()) : null;
        String remark = (String) params.get("remark");
        Long productId = params.get("productId") != null ? Long.valueOf(params.get("productId").toString()) : null;
        Integer quantity = params.get("quantity") != null ? Integer.valueOf(params.get("quantity").toString()) : 1;
        
        try {
            Order order = orderService.createOrderFromProduct(userId, addressId, remark, productId, quantity);
            log.info("立即购买订单创建成功 - orderId: {}, orderNo: {}", order.getId(), order.getOrderNo());
            return Result.success(order);
        } catch (RuntimeException e) {
            log.error("立即购买订单创建失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/pay/{id}")
    public Result pay(@RequestHeader(value = "Authorization", required = false) String token,
                     @PathVariable Long id) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        
        boolean success = orderService.payOrder(id, userId);
        if (!success) return Result.error("支付失败");
        
        // ✨ 支付成功后，保存AI祝福消息
        if (redisChatService != null) {
            Order order = orderService.getById(id);
            if (order != null) {
                String blessing = generateBlessing(order);
                redisChatService.saveUserMessage(userId, "assistant", blessing);
                log.info("支付成功AI祝福已保存 - userId: {}, orderNo: {}", userId, order.getOrderNo());
            }
        }
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("message", "支付成功");
        result.put("blessing", generateBlessing(orderService.getById(id)));
        return Result.success(result);
    }
    
    /**
     * 生成AI祝福消息
     */
    private String generateBlessing(Order order) {
        if (order == null) {
            return "🌸 您的订单已支付成功！您的花正在路上，期待给TA一个惊喜吧～";
        }
        
        String[] blessings = {
            "🌸 您的花已在路上！预计1-2天送达，祝您和收花人都有美好的一天～",
            "💐 订单支付成功！我们会精心包装每一束花，让爱意完美传递～",
            "🌹 感谢您的信任！您的心意即将启程，愿这份美好能温暖TA的心～",
            "🌷 支付成功！您的鲜花订单已提交，我们会尽快安排配送～",
            "🌺 太棒了！您的花束正在准备中，很快就能送到TA手中啦～",
            "💖 订单已确认！我们会用心呵护每一朵花，让爱传递到TA身边～"
        };
        
        // 根据订单金额或时间选择不同祝福
        int index = (int) (System.currentTimeMillis() % blessings.length);
        return blessings[index];
    }

    @PostMapping("/cancel/{id}")
    public Result cancel(@RequestHeader(value = "Authorization", required = false) String token,
                        @PathVariable Long id) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        boolean success = orderService.cancelOrder(id, userId);
        if (!success) return Result.error("取消失败");
        return Result.success("取消成功");
    }

    @GetMapping("/list")
    public Result list(@RequestHeader(value = "Authorization", required = false) String token,
                      @RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int size,
                      @RequestParam(required = false) Object status) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        
        log.info("查询订单列表 - userId: {}, page: {}, size: {}, status: {}", userId, page, size, status);
        
        Integer statusInt = null;
        if (status != null) {
            try {
                if (status instanceof Number) {
                    statusInt = ((Number) status).intValue();
                } else {
                    statusInt = Integer.valueOf(status.toString());
                }
            } catch (Exception e) {
                log.warn("状态参数解析失败: {}", status);
            }
        }
        
        return Result.success(orderService.getUserOrders(userId, page, size, statusInt));
    }

    @GetMapping("/detail/{id}")
    public Result detail(@RequestHeader(value = "Authorization", required = false) String token,
                       @PathVariable Long id) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        Order order = orderService.getById(id);
        if (order == null || !order.getUserId().equals(userId)) return Result.error("订单不存在");
        order.setOrderItems(orderService.getOrderItems(id));
        return Result.success(order);
    }

    @PostMapping("/receive/{id}")
    public Result receive(@RequestHeader(value = "Authorization", required = false) String token,
                         @PathVariable Long id) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        orderService.updateStatus(id, 3);
        return Result.success("确认收货成功");
    }

    private Long extractUserId(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return null;
            }
            return JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            log.warn("Token解析失败", e);
            return null;
        }
    }
}
