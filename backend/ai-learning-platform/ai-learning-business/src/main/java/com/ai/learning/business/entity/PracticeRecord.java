package com.ai.learning.business.entity;

import com.ai.learning.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pra_practice_record")
public class PracticeRecord extends BaseEntity {

    private Long userId;
    
    private Long subjectId;
    
    private String practiceType;
    
    private String questionIds;
    
    private Integer currentIndex;
    
    private Integer totalCount;
    
    private Integer answeredCount;
    
    private Integer correctCount;
    
    private String score;
    
    private Integer duration;
    
    private String status;
    
    private String startTime;
    
    private String submitTime;
}
