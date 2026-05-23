package com.ai.learning.dto;

import lombok.Data;

@Data
public class AiAnalyzeRequest {
    private Long questionId;
    private String userAnswer;
    private String correctAnswer;
}
