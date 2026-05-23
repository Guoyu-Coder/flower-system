package com.ai.learning.agent.dto;

import lombok.Data;

@Data
public class TaskPlanningResult {
    private Object dailyPlan;
    private Object weeklyGoals;
    private Object keyPoints;
    private String practicePlan;
    private String reviewPlan;
}
