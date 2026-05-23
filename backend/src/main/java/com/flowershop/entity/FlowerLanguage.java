package com.flowershop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("flower_language")
public class FlowerLanguage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String flowerName;
    private String language;
    private String meaning;
    private String story;
    private String occasion;
    private String suitableFor;
    private String taboo;
    private String colorMeaning;
    private String image;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public Object getName() {
        return null;
    }

    public Object getFlowerLanguage() {
        return null;
    }

    public Object getAlias() {
        return null;
    }

    public Object getTags() {
        return null;
    }

    public Object getCategory() {
        return null;
    }
}