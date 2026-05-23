package com.ai.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("learning_report")
public class LearningReport {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String reportType;

    private String reportData;

    private String summary;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
