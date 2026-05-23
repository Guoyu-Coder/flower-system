package com.ai.learning.agent.dto;

import lombok.Data;

@Data
public class AnswerGradingRequest {
    private String questionType;
    private String questionContent;
    private String correctAnswer;
    private Double score;
    private String userAnswer;
}
