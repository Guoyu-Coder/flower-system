package com.ai.learning.business.entity;

import com.ai.learning.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qms_question")
public class Question extends BaseEntity {

    private Long subjectId;
    
    private Long chapterId;
    
    private String knowledgePointIds;
    
    private String type;
    
    private Integer difficulty;
    
    private String content;
    
    private String options;
    
    private String answer;
    
    private String analysis;
    
    private String points;
    
    private String tags;
    
    private String errorTags;
    
    private String examPoint;
    
    private String source;
    
    private Integer usageCount;
    
    private String correctRate;
    
    private Integer status;
    
    private Long createUserId;
    
    private Long reviewUserId;
    
    private String reviewTime;
    
    private String reviewMsg;
}
