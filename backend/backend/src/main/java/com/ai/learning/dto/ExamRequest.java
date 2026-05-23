package com.ai.learning.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExamRequest {
    private String examTitle;
    private String subject;
    private Integer totalScore;
    private List<Long> questionIds;
}
