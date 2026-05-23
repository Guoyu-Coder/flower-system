package com.ai.learning.agent.service.impl;

import com.ai.learning.agent.client.DeepSeekClient;
import com.ai.learning.agent.dto.*;
import com.ai.learning.agent.prompt.PromptTemplate;
import com.ai.learning.agent.service.AiAgentService;
import com.ai.learning.common.exception.BusinessException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AiAgentServiceImpl implements AiAgentService {

    private final DeepSeekClient deepSeekClient;

    public AiAgentServiceImpl(DeepSeekClient deepSeekClient) {
        this.deepSeekClient = deepSeekClient;
    }

    @Override
    public List<QuestionGenerateResult> generateQuestions(QuestionGenerateRequest request) {
        if (!deepSeekClient.isConfigured()) {
            throw BusinessException.of("AI服务未配置，请联系管理员");
        }

        String prompt = PromptTemplate.getQuestionGeneratorPrompt(
            request.getSubjectName(),
            request.getKnowledgePoints(),
            request.getDifficulty(),
            request.getQuestionTypes(),
            request.getQuestionCount()
        );

        try {
            String response = deepSeekClient.chatWithSystemPrompt(
                "你是一个专业的出题专家，生成高质量的练习题。",
                prompt
            );

            return parseQuestionsFromResponse(response);
        } catch (Exception e) {
            log.error("AI生成题目失败", e);
            throw BusinessException.of("AI生成题目失败: " + e.getMessage());
        }
    }

    @Override
    public AnswerGradingResult gradeAnswer(AnswerGradingRequest request) {
        if (!deepSeekClient.isConfigured()) {
            throw BusinessException.of("AI服务未配置，请联系管理员");
        }

        String prompt = PromptTemplate.getAnswerGraderPrompt(
            request.getQuestionType(),
            request.getQuestionContent(),
            request.getCorrectAnswer(),
            request.getScore(),
            request.getUserAnswer()
        );

        try {
            String response = deepSeekClient.chatWithSystemPrompt(
                "你是一个严格的批改老师，客观公正地评价学生答案。",
                prompt
            );

            return parseGradingResultFromResponse(response);
        } catch (Exception e) {
            log.error("AI批改答案失败", e);
            throw BusinessException.of("AI批改答案失败: " + e.getMessage());
        }
    }

    @Override
    public ErrorDiagnosisResult diagnoseError(ErrorDiagnosisRequest request) {
        if (!deepSeekClient.isConfigured()) {
            throw BusinessException.of("AI服务未配置，请联系管理员");
        }

        String prompt = PromptTemplate.getErrorDiagnosisPrompt(
            request.getQuestionContent(),
            request.getUserAnswer(),
            request.getCorrectAnswer(),
            request.getKnowledgePoints()
        );

        try {
            String response = deepSeekClient.chatWithSystemPrompt(
                "你是一个专业的学习诊断师，准确分析学生的错误原因。",
                prompt
            );

            return parseDiagnosisResultFromResponse(response);
        } catch (Exception e) {
            log.error("AI错题诊断失败", e);
            throw BusinessException.of("AI错题诊断失败: " + e.getMessage());
        }
    }

    @Override
    public LearningAnalysisResult analyzeLearning(LearningAnalysisRequest request) {
        if (!deepSeekClient.isConfigured()) {
            throw BusinessException.of("AI服务未配置，请联系管理员");
        }

        String prompt = PromptTemplate.getLearningAnalysisPrompt(
            request.getUserId(),
            request.getRecentPracticeData(),
            request.getKnowledgeMasteryData(),
            request.getLearningDuration()
        );

        try {
            String response = deepSeekClient.chatWithSystemPrompt(
                "你是一个专业的学习分析师，提供个性化的学习建议。",
                prompt
            );

            return parseLearningAnalysisResultFromResponse(response);
        } catch (Exception e) {
            log.error("AI学情分析失败", e);
            throw BusinessException.of("AI学情分析失败: " + e.getMessage());
        }
    }

    @Override
    public String answerQuestion(QARequest request) {
        if (!deepSeekClient.isConfigured()) {
            throw BusinessException.of("AI服务未配置，请联系管理员");
        }

        String prompt = PromptTemplate.getQAPrompt(
            request.getQuestion(),
            request.getRelatedKnowledge()
        );

        try {
            return deepSeekClient.chatWithSystemPrompt(
                "你是一个耐心的学习辅导老师，用通俗易懂的语言解答学生问题。",
                prompt
            );
        } catch (Exception e) {
            log.error("AI答疑失败", e);
            throw BusinessException.of("AI答疑失败: " + e.getMessage());
        }
    }

    @Override
    public TaskPlanningResult planTask(TaskPlanningRequest request) {
        if (!deepSeekClient.isConfigured()) {
            throw BusinessException.of("AI服务未配置，请联系管理员");
        }

        String prompt = PromptTemplate.getTaskPlanningPrompt(
            request.getUserLevel(),
            request.getTargetSubject(),
            request.getAvailableTime(),
            request.getLearningGoal()
        );

        try {
            String response = deepSeekClient.chatWithSystemPrompt(
                "你是一个专业的学习规划师，制定科学合理的学习计划。",
                prompt
            );

            return parseTaskPlanningResultFromResponse(response);
        } catch (Exception e) {
            log.error("AI学习规划失败", e);
            throw BusinessException.of("AI学习规划失败: " + e.getMessage());
        }
    }

    private List<QuestionGenerateResult> parseQuestionsFromResponse(String response) {
        try {
            JSONArray jsonArray = JSON.parseArray(extractJsonFromResponse(response));
            List<QuestionGenerateResult> results = new ArrayList<>();
            
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                QuestionGenerateResult result = new QuestionGenerateResult();
                result.setType(obj.getString("type"));
                result.setContent(obj.getString("content"));
                result.setOptions(obj.getJSONArray("options"));
                result.setAnswer(obj.getString("answer"));
                result.setAnalysis(obj.getString("analysis"));
                result.setDifficulty(obj.getInteger("difficulty"));
                result.setKnowledgePoints(obj.getString("knowledgePoints"));
                result.setExamPoint(obj.getString("examPoint"));
                results.add(result);
            }
            
            return results;
        } catch (Exception e) {
            log.error("解析题目JSON失败: {}", response, e);
            throw BusinessException.of("解析AI生成结果失败");
        }
    }

    private AnswerGradingResult parseGradingResultFromResponse(String response) {
        try {
            JSONObject json = JSON.parseObject(extractJsonFromResponse(response));
            AnswerGradingResult result = new AnswerGradingResult();
            result.setIsCorrect(json.getBoolean("isCorrect"));
            result.setScore(json.getDouble("score"));
            result.setMaxScore(json.getDouble("maxScore"));
            result.setEvaluation(json.getString("evaluation"));
            result.setErrorAnalysis(json.getString("errorAnalysis"));
            result.setCorrectGuidance(json.getString("correctGuidance"));
            result.setImprovement(json.getString("improvement"));
            return result;
        } catch (Exception e) {
            log.error("解析批改结果JSON失败: {}", response, e);
            throw BusinessException.of("解析AI批改结果失败");
        }
    }

    private ErrorDiagnosisResult parseDiagnosisResultFromResponse(String response) {
        try {
            JSONObject json = JSON.parseObject(extractJsonFromResponse(response));
            ErrorDiagnosisResult result = new ErrorDiagnosisResult();
            result.setErrorType(json.getString("errorType"));
            result.setErrorReason(json.getString("errorReason"));
            result.setWeakKnowledgePoints(json.getJSONArray("weakKnowledgePoints").toList(String.class));
            result.setDiagnosis(json.getString("diagnosis"));
            result.setSuggestions(json.getJSONArray("suggestions").toList(String.class));
            return result;
        } catch (Exception e) {
            log.error("解析诊断结果JSON失败: {}", response, e);
            throw BusinessException.of("解析AI诊断结果失败");
        }
    }

    private LearningAnalysisResult parseLearningAnalysisResultFromResponse(String response) {
        try {
            JSONObject json = JSON.parseObject(extractJsonFromResponse(response));
            LearningAnalysisResult result = new LearningAnalysisResult();
            result.setSummary(json.getString("summary"));
            result.setStrengths(json.getJSONArray("strengths").toList(String.class));
            result.setWeaknesses(json.getJSONArray("weaknesses").toList(String.class));
            result.setCorrectRate(json.getDouble("correctRate"));
            result.setImprovement(json.getString("improvement"));
            result.setSuggestions(json.getJSONArray("suggestions").toList(String.class));
            result.setNextPlan(json.getString("nextPlan"));
            return result;
        } catch (Exception e) {
            log.error("解析学习分析结果JSON失败: {}", response, e);
            throw BusinessException.of("解析AI学习分析结果失败");
        }
    }

    private TaskPlanningResult parseTaskPlanningResultFromResponse(String response) {
        try {
            JSONObject json = JSON.parseObject(extractJsonFromResponse(response));
            TaskPlanningResult result = new TaskPlanningResult();
            result.setDailyPlan(json.getJSONObject("dailyPlan"));
            result.setWeeklyGoals(json.getJSONArray("weeklyGoals"));
            result.setKeyPoints(json.getJSONArray("keyPoints"));
            result.setPracticePlan(json.getString("practicePlan"));
            result.setReviewPlan(json.getString("reviewPlan"));
            return result;
        } catch (Exception e) {
            log.error("解析学习规划结果JSON失败: {}", response, e);
            throw BusinessException.of("解析AI学习规划结果失败");
        }
    }

    private String extractJsonFromResponse(String response) {
        response = response.trim();
        int startIndex = response.indexOf('[');
        if (startIndex == -1) {
            startIndex = response.indexOf('{');
        }
        
        if (startIndex != -1) {
            return response.substring(startIndex);
        }
        
        return response;
    }
}
