package com.ai.learning.dto;

import lombok.Data;

@Data
public class AiGenerateRequest {
    private String subject;
    private Integer difficulty;
    private Integer count;
    private String requirement;
}
