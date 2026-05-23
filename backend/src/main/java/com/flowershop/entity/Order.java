package com.flowershop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("order_no")
    private String orderNo;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("address_id")
    private Long addressId;
    
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    @TableField("discount_amount")
    private BigDecimal discountAmount;
    
    @TableField("pay_amount")
    private BigDecimal payAmount;
    
    @TableField("pay_method")
    private Integer payMethod;
    
    @TableField("status")
    private Integer status;
    
    @TableField("remark")
    private String remark;
    
    @TableField("receiver_name")
    private String receiverName;
    
    @TableField("receiver_phone")
    private String receiverPhone;
    
    @TableField("receiver_address")
    private String receiverAddress;
    
    @TableField("pay_time")
    private LocalDateTime payTime;
    
    @TableField("delivery_time")
    private LocalDateTime deliveryTime;
    
    @TableField("received_time")
    private LocalDateTime receivedTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
    
    @TableField(exist = false)
    private List<OrderItem> orderItems;
}
