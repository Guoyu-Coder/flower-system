package com.ai.learning.business.entity;

import com.ai.learning.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("err_wrong_question")
public class WrongQuestion extends BaseEntity {

    private Long userId;
    
    private Long questionId;
    
    private Long practiceRecordId;
    
    private Long examRecordId;
    
    private String userAnswer;
    
    private String correctAnswer;
    
    private String errorType;
    
    private String knowledgePointIds;
    
    private Integer errorTimes;
    
    private String lastErrorTime;
    
    private Integer masteryLevel;
    
    private Integer isKeyPoint;
    
    private Integer isCollected;
    
    private Integer reviewCount;
    
    private String lastReviewTime;
    
    private String nextReviewTime;
    
    private String aiDiagnosis;
}
