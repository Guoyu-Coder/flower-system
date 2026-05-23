package com.ai.learning.agent.dto;

import lombok.Data;
import java.util.List;

@Data
public class LearningAnalysisResult {
    private String summary;
    private List<String> strengths;
    private List<String> weaknesses;
    private Double correctRate;
    private String improvement;
    private List<String> suggestions;
    private String nextPlan;
}
