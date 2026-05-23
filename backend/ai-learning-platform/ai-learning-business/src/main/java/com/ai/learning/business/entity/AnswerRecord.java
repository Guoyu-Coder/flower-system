package com.ai.learning.business.entity;

import com.ai.learning.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pra_answer_record")
public class AnswerRecord extends BaseEntity {

    private Long practiceId;
    
    private Long userId;
    
    private Long questionId;
    
    private String userAnswer;
    
    private String correctAnswer;
    
    private Integer isCorrect;
    
    private String score;
    
    private Integer duration;
    
    private String submitTime;
    
    private String aiEvaluation;
    
    private String aiAnalysis;
    
    private String knowledgePointIds;
    
    private String errorType;
}
