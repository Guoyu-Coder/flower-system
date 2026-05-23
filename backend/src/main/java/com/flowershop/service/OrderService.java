package com.flowershop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flowershop.entity.*;
import com.flowershop.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private AddressService addressService;

    @Transactional
    public Order createOrder(Long userId, Long addressId, String remark, List<Long> cartIds) {
        Address address = null;
        if (addressId != null) {
            address = addressService.getById(addressId);
        }

        List<Cart> selectedCart;
        if (cartIds != null && !cartIds.isEmpty()) {
            selectedCart = cartService.listByIds(cartIds);
            selectedCart = selectedCart.stream()
                    .filter(c -> c.getUserId().equals(userId))
                    .collect(Collectors.toList());
        } else {
            selectedCart = cartService.getSelectedCartItems(userId);
        }
        
        if (selectedCart.isEmpty()) {
            throw new RuntimeException("请选择要结算的商品");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart c : selectedCart) {
            Product p = productService.getById(c.getProductId());
            BigDecimal price = (p != null && p.getDiscountPrice() != null) ? p.getDiscountPrice() : c.getPrice();
            price = price != null ? price : (p != null ? p.getPrice() : BigDecimal.ZERO);
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(c.getQuantity())));
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(addressId);
        if (address != null) {
            order.setReceiverName(address.getReceiverName());
            order.setReceiverPhone(address.getReceiverPhone());
            String fullAddress = (address.getProvince() != null ? address.getProvince() : "") + 
                                (address.getCity() != null ? address.getCity() : "") + 
                                (address.getDistrict() != null ? address.getDistrict() : "") + 
                                (address.getDetailAddress() != null ? address.getDetailAddress() : "");
            order.setReceiverAddress(fullAddress);
        }
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayAmount(totalAmount);
        order.setStatus(0);
        order.setRemark(remark);
        save(order);

        for (Cart c : selectedCart) {
            Product p = productService.getById(c.getProductId());
            if (p != null) {
                OrderItem item = new OrderItem();
                item.setOrderId(order.getId());
                item.setProductId(p.getId());
                item.setProductName(p.getName());
                item.setProductImage(p.getCoverImage());
                BigDecimal price = p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice();
                item.setPrice(price);
                item.setQuantity(c.getQuantity());
                item.setSubtotal(price.multiply(BigDecimal.valueOf(c.getQuantity())));
                orderItemService.save(item);

                p.setStock(p.getStock() - c.getQuantity());
                p.setSales(p.getSales() + c.getQuantity());
                productService.updateById(p);
            }
        }

        for (Cart c : selectedCart) {
            cartService.removeById(c.getId());
        }

        return order;
    }

    @Transactional
    public Order createOrderFromProduct(Long userId, Long addressId, String remark, Long productId, Integer quantity) {
        Address address = null;
        if (addressId != null) {
            address = addressService.getById(addressId);
        }

        Product p = productService.getById(productId);
        if (p == null) {
            throw new RuntimeException("商品不存在");
        }
        if (p.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        BigDecimal price = p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice();
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(quantity));

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(addressId);
        if (address != null) {
            order.setReceiverName(address.getReceiverName());
            order.setReceiverPhone(address.getReceiverPhone());
            String fullAddress = (address.getProvince() != null ? address.getProvince() : "") + 
                                (address.getCity() != null ? address.getCity() : "") + 
                                (address.getDistrict() != null ? address.getDistrict() : "") + 
                                (address.getDetailAddress() != null ? address.getDetailAddress() : "");
            order.setReceiverAddress(fullAddress);
        }
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayAmount(totalAmount);
        order.setStatus(0);
        order.setRemark(remark);
        save(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(p.getId());
        item.setProductName(p.getName());
        item.setProductImage(p.getCoverImage());
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setSubtotal(price.multiply(BigDecimal.valueOf(quantity)));
        orderItemService.save(item);

        p.setStock(p.getStock() - quantity);
        p.setSales(p.getSales() + quantity);
        productService.updateById(p);

        return order;
    }

    @Transactional
    public boolean payOrder(Long orderId, Long userId) {
        Order order = lambdaQuery()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId)
                .one();
        if (order == null || order.getStatus() != 0) {
            return false;
        }
        order.setStatus(1);
        order.setPayMethod(1);
        order.setPayTime(LocalDateTime.now());
        return updateById(order);
    }

    public boolean cancelOrder(Long orderId, Long userId) {
        Order order = lambdaQuery()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId)
                .one();
        if (order == null || order.getStatus() > 1) {
            return false;
        }
        order.setStatus(4);
        return updateById(order);
    }

    public boolean updateStatus(Long orderId, Integer status) {
        Order order = getById(orderId);
        if (order == null) return false;
        order.setStatus(status);
        if (status == 2) {
            order.setDeliveryTime(LocalDateTime.now());
        }
        if (status == 3) {
            order.setReceivedTime(LocalDateTime.now());
        }
        return updateById(order);
    }

    public Page<Order> getUserOrders(Long userId, int page, int size, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        Page<Order> pageResult = page(new Page<>(page, size), wrapper);
        for (Order o : pageResult.getRecords()) {
            o.setOrderItems(getOrderItems(o.getId()));
        }
        return pageResult;
    }

    public Page<Order> getAllOrders(int page, int size, Integer status) {
        return getAllOrders(page, size, status, null);
    }

    public Page<Order> getAllOrders(int page, int size, Integer status, String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        return page(new Page<>(page, size), wrapper);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderId, orderId)
                .list();
    }

    public Map<String, Object> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Order> allOrders = list();
        stats.put("total", allOrders.size());
        stats.put("pendingPayment", allOrders.stream().filter(o -> o.getStatus() != null && o.getStatus() == 0).count());
        stats.put("pendingDelivery", allOrders.stream().filter(o -> o.getStatus() != null && o.getStatus() == 1).count());
        stats.put("shipped", allOrders.stream().filter(o -> o.getStatus() != null && o.getStatus() == 2).count());
        stats.put("completed", allOrders.stream().filter(o -> o.getStatus() != null && o.getStatus() == 3).count());
        stats.put("cancelled", allOrders.stream().filter(o -> o.getStatus() != null && o.getStatus() == 4).count());
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus() >= 1 && o.getStatus() <= 3)
                .map(Order::getPayAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);
        return stats;
    }

    private String generateOrderNo() {
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "FL" + timeStr + uuid;
    }
}
