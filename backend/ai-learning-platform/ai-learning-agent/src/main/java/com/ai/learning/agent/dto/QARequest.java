package com.ai.learning.agent.dto;

import lombok.Data;

@Data
public class QARequest {
    private Long userId;
    private String question;
    private String relatedKnowledge;
    private Long sessionId;
}
