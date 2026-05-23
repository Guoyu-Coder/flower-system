package com.flowershop.controller;

import com.flowershop.common.Result;
import com.flowershop.entity.Order;
import com.flowershop.entity.Product;
import com.flowershop.entity.User;
import com.flowershop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/stats")
    public Result stats() {
        Map<String, Object> stats = new HashMap<>();

        // 订单统计
        List<Order> allOrders = orderService.list();
        long totalOrders = allOrders.size();
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() >= 1)
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long pendingOrders = allOrders.stream().filter(o -> o.getStatus() == 0).count();

        // 用户统计
        long totalUsers = userService.count();

        // 商品统计
        long totalProducts = productService.count();

        // 分类统计
        long totalCategories = categoryService.count();

        // 各状态订单数
        Map<String, Long> orderStatusMap = new HashMap<>();
        orderStatusMap.put("待付款", allOrders.stream().filter(o -> o.getStatus() == 0).count());
        orderStatusMap.put("待发货", allOrders.stream().filter(o -> o.getStatus() == 1).count());
        orderStatusMap.put("已发货", allOrders.stream().filter(o -> o.getStatus() == 2).count());
        orderStatusMap.put("已完成", allOrders.stream().filter(o -> o.getStatus() == 3).count());
        orderStatusMap.put("已取消", allOrders.stream().filter(o -> o.getStatus() == 4).count());

        // 月销售额（模拟最近7天）
        List<Map<String, Object>> dailySales = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            cal.add(Calendar.DAY_OF_YEAR, - (i == 6 ? 0 : 1));
            Map<String, Object> day = new HashMap<>();
            day.put("date", String.format("%d-%02d-%02d",
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));
            day.put("amount", totalRevenue.divide(BigDecimal.valueOf(7), 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(new Random().nextDouble() * 0.5 + 0.75)));
            dailySales.add(day);
        }

        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("pendingOrders", pendingOrders);
        stats.put("totalUsers", totalUsers);
        stats.put("totalProducts", totalProducts);
        stats.put("totalCategories", totalCategories);
        stats.put("orderStatusMap", orderStatusMap);
        stats.put("dailySales", dailySales);

        return Result.success(stats);
    }
}