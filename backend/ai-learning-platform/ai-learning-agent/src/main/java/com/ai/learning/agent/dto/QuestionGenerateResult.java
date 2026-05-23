package com.ai.learning.agent.dto;

import lombok.Data;

@Data
public class QuestionGenerateResult {
    private String type;
    private String content;
    private Object options;
    private String answer;
    private String analysis;
    private Integer difficulty;
    private String knowledgePoints;
    private String examPoint;
}
