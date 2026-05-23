package com.ai.learning.agent.dto;

import lombok.Data;

@Data
public class QuestionGenerateRequest {
    private Long subjectId;
    private String subjectName;
    private String knowledgePoints;
    private Integer difficulty;
    private String questionTypes;
    private Integer questionCount;
    private Double totalScore;
}
