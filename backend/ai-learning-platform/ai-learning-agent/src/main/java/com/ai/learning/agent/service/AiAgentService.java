package com.ai.learning.agent.service;

import com.ai.learning.agent.dto.*;
import java.util.List;

public interface AiAgentService {

    List<QuestionGenerateResult> generateQuestions(QuestionGenerateRequest request);

    AnswerGradingResult gradeAnswer(AnswerGradingRequest request);

    ErrorDiagnosisResult diagnoseError(ErrorDiagnosisRequest request);

    LearningAnalysisResult analyzeLearning(LearningAnalysisRequest request);

    String answerQuestion(QARequest request);

    TaskPlanningResult planTask(TaskPlanningRequest request);
}
