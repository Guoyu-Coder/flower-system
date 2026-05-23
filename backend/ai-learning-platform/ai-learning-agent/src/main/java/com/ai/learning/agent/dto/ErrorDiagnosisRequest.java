package com.ai.learning.agent.dto;

import lombok.Data;

@Data
public class ErrorDiagnosisRequest {
    private String questionContent;
    private String userAnswer;
    private String correctAnswer;
    private String knowledgePoints;
}
