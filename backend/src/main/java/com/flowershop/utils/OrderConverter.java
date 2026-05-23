package com.flowershop.utils;

import com.flowershop.dto.response.OrderDTO;
import com.flowershop.dto.response.OrderItemDTO;
import com.flowershop.entity.Order;
import com.flowershop.entity.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderConverter {

    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;
        
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setAddressId(order.getAddressId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setPayAmount(order.getPayAmount());
        dto.setPayMethod(order.getPayMethod());
        dto.setStatus(order.getStatus());
        dto.setRemark(order.getRemark());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setPayTime(order.getPayTime());
        dto.setDeliveryTime(order.getDeliveryTime());
        dto.setReceivedTime(order.getReceivedTime());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        return dto;
    }

    public static OrderDTO toDTOWithItems(Order order, List<OrderItem> items) {
        OrderDTO dto = toDTO(order);
        if (items != null && !items.isEmpty()) {
            dto.setItems(items.stream().map(OrderConverter::toItemDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    public static OrderItemDTO toItemDTO(OrderItem item) {
        if (item == null) return null;
        
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setOrderId(item.getOrderId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setProductImage(item.getProductImage());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());
        
        return dto;
    }
}
