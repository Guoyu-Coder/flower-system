package com.ai.learning.business.entity;

import com.ai.learning.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_exam_record")
public class ExamRecord extends BaseEntity {

    private Long examId;
    
    private Long userId;
    
    private String answerRecords;
    
    private String totalScore;
    
    private Integer correctCount;
    
    private Integer totalCount;
    
    private Integer isPassed;
    
    private Integer duration;
    
    private String startTime;
    
    private String submitTime;
    
    private Integer isAutomaticallySubmit;
    
    private String status;
}
