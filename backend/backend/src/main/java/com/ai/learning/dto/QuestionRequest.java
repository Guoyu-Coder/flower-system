package com.ai.learning.dto;

import lombok.Data;

@Data
public class QuestionRequest {
    private String title;
    private String content;
    private Integer type;
    private Integer difficulty;
    private String subject;
    private String answer;
    private String analysis;
}
