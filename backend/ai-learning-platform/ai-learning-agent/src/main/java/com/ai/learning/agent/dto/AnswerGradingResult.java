package com.ai.learning.agent.dto;

import lombok.Data;

@Data
public class AnswerGradingResult {
    private Boolean isCorrect;
    private Double score;
    private Double maxScore;
    private String evaluation;
    private String errorAnalysis;
    private String correctGuidance;
    private String improvement;
}
