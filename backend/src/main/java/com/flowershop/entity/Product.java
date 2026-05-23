package com.flowershop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String name;
    private String subtitle;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stock;
    private Integer sales;
    private String coverImage;
    private String images;
    private String specifications;
    private Integer isNew;
    private Integer isHot;
    private Integer isRecommend;
    private Integer status;
    private String tags;
    private String flowerLanguage;
    private String occasion;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String categoryName;

    public void setRelatedProducts(List<Product> related) {
    }
}