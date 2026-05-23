package com.flowershop.controller;

import com.flowershop.common.Result;
import com.flowershop.entity.Order;
import com.flowershop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) Integer status) {
        return Result.success(orderService.getAllOrders(page, size, status));
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) return Result.error("订单不存在");
        order.setOrderItems(orderService.getOrderItems(id));
        return Result.success(order);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        String expressNo = (String) params.get("expressNo");
        orderService.updateStatus(id, status);
        return Result.success("更新成功");
    }

    @GetMapping("/stats")
    public Result stats() {
        return Result.success(orderService.getOrderStats());
    }
}