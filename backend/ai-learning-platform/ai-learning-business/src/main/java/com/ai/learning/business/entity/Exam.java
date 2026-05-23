package com.ai.learning.business.entity;

import com.ai.learning.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_exam")
public class Exam extends BaseEntity {

    private String title;
    
    private Long subjectId;
    
    private String examType;
    
    private String totalScore;
    
    private String passingScore;
    
    private Integer duration;
    
    private Integer totalQuestions;
    
    private String questionIds;
    
    private String generateType;
    
    private String aiPrompt;
    
    private String status;
    
    private String startTime;
    
    private String endTime;
    
    private Integer allowReview;
    
    private String allowReviewTime;
    
    private Long createUserId;
}
