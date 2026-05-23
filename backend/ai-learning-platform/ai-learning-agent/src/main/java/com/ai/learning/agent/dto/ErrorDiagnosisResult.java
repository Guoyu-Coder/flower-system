package com.ai.learning.agent.dto;

import lombok.Data;
import java.util.List;

@Data
public class ErrorDiagnosisResult {
    private String errorType;
    private String errorReason;
    private List<String> weakKnowledgePoints;
    private String diagnosis;
    private List<String> suggestions;
}
