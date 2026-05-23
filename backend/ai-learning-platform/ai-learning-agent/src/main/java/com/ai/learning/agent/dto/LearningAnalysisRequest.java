package com.ai.learning.agent.dto;

import lombok.Data;

@Data
public class LearningAnalysisRequest {
    private Long userId;
    private String recentPracticeData;
    private String knowledgeMasteryData;
    private String learningDuration;
}
