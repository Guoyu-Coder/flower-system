package com.ai.learning.agent.dto;

import lombok.Data;

@Data
public class TaskPlanningRequest {
    private Long userId;
    private String userLevel;
    private String targetSubject;
    private String availableTime;
    private String learningGoal;
}
