package com.ai.learning.dto;

import lombok.Data;
import java.util.List;

@Data
public class AnswerSubmitRequest {
    private Long examId;
    private List<QuestionAnswer> answers;

    @Data
    public static class QuestionAnswer {
        private Long questionId;
        private String userAnswer;
    }
}
