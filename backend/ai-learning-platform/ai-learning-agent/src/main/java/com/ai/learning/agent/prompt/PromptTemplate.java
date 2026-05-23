package com.ai.learning.agent.prompt;

public class PromptTemplate {

    public static String getQuestionGeneratorPrompt(String subjectName, String knowledgePoints, 
                                                   int difficulty, String questionTypes, 
                                                   int questionCount) {
        return String.format("""
            你是一个专业的出题专家。请根据以下要求生成练习题：

            ## 出题要求
            - 学科：%s
            - 知识点：%s
            - 难度等级：%d (1=简单, 2=中等, 3=困难)
            - 题目类型：%s
            - 题目数量：%d
            - 总分：100分（平均分配）

            ## 题目类型说明
            - single_choice: 单选题
            - multi_choice: 多选题
            - true_false: 判断题
            - fill_blank: 填空题
            - short_answer: 简答题

            ## 输出要求
            请以JSON数组格式返回，示例：
            [
              {
                "type": "single_choice",
                "content": "题目内容",
                "options": [
                  {"key": "A", "value": "选项A内容", "isCorrect": true},
                  {"key": "B", "value": "选项B内容", "isCorrect": false},
                  {"key": "C", "value": "选项C内容", "isCorrect": false},
                  {"key": "D", "value": "选项D内容", "isCorrect": false}
                ],
                "answer": "A",
                "analysis": "答案解析",
                "difficulty": %d,
                "knowledgePoints": ["知识点1", "知识点2"],
                "examPoint": "考点说明"
              }
            ]

            ## 注意事项
            1. 题目要符合教学大纲要求
            2. 答案必须准确无误
            3. 解析要详细清楚
            4. 难度要符合要求
            5. 单选题必须有4个选项
            6. 多选题答案可能是多个选项
            7. 判断题不需要选项
            8. 只返回JSON数组，不要其他内容
            """, subjectName, knowledgePoints, difficulty, questionTypes, questionCount, difficulty);
    }

    public static String getAnswerGraderPrompt(String questionType, String questionContent,
                                                String correctAnswer, double score,
                                                String userAnswer) {
        return String.format("""
            你是一个严格的批改老师。请批改以下学生答案：

            ## 题目信息
            - 题目类型：%s
            - 题目内容：%s
            - 标准答案：%s
            - 分值：%.1f分

            ## 学生答案
            %s

            ## 批改要求
            请以JSON格式返回：
            {
              "isCorrect": true/false,
              "score": 分数,
              "maxScore": 满分,
              "evaluation": "总体评价",
              "errorAnalysis": "错误分析（如果答错）",
              "correctGuidance": "正确解题思路",
              "improvement": "提升建议"
            }

            ## 评分标准
            - 客观题（单选/多选/判断）：完全正确得满分，错误得0分
            - 主观题：根据答案完整度和正确性按比例给分
            """, questionType, questionContent, correctAnswer, score, userAnswer);
    }

    public static String getErrorDiagnosisPrompt(String questionContent, String userAnswer,
                                                  String correctAnswer, String knowledgePoints) {
        return String.format("""
            请分析以下错题，诊断学生的错误类型和薄弱知识点：

            ## 错题信息
            - 题目：%s
            - 学生答案：%s
            - 正确答案：%s
            - 知识点：%s

            ## 错误类型分类
            - misunderstanding: 概念混淆
            - calculation: 计算失误
            - misread: 审题错误
            - blind_spot: 知识点盲区

            ## 诊断要求
            请以JSON格式返回：
            {
              "errorType": "错误类型",
              "errorReason": "具体错误原因",
              "weakKnowledgePoints": ["薄弱知识点列表"],
              "diagnosis": "详细诊断分析",
              "suggestions": ["改进建议1", "改进建议2"]
            }

            ## 分析要求
            1. 准确判断错误类型
            2. 找出学生真正薄弱的地方
            3. 提供切实可行的改进建议
            """, questionContent, userAnswer, correctAnswer, knowledgePoints);
    }

    public static String getLearningAnalysisPrompt(Long userId, String recentPracticeData,
                                                    String knowledgeMasteryData,
                                                    String learningDuration) {
        return String.format("""
            请分析以下学生的学习数据，生成个性化学习报告：

            ## 学生信息
            - 用户ID：%d

            ## 最近练习数据
            %s

            ## 知识点掌握度
            %s

            ## 学习时长
            %s

            ## 分析要求
            请以JSON格式返回：
            {
              "summary": "整体学习情况总结",
              "strengths": ["强项1", "强项2"],
              "weaknesses": ["弱项1", "弱项2"],
              "correctRate": 正确率,
              "improvement": "相比上次的进步",
              "suggestions": ["建议1", "建议2"],
              "nextPlan": "下一步学习计划"
            }

            ## 分析维度
            1. 整体正确率趋势
            2. 各知识点掌握情况
            3. 学习效率分析
            4. 薄弱点识别
            5. 个性化建议
            """, userId, recentPracticeData, knowledgeMasteryData, learningDuration);
    }

    public static String getQAPrompt(String question, String relatedKnowledge) {
        return String.format("""
            你是一个耐心的学习辅导老师。请回答学生的问题，并尽量结合知识点进行讲解。

            ## 学生问题
            %s

            ## 相关知识点（可选参考）
            %s

            ## 回答要求
            1. 回答要通俗易懂
            2. 尽量结合具体例子讲解
            3. 如果有相关知识点，要关联讲解
            4. 适当提问引导学生思考
            5. 提供类似的练习题帮助巩固

            请用自然流畅的语言回答：
            """, question, relatedKnowledge != null ? relatedKnowledge : "无特定知识点关联");
    }

    public static String getTaskPlanningPrompt(String userLevel, String targetSubject,
                                                String availableTime, String learningGoal) {
        return String.format("""
            请为学生制定个性化的学习计划：

            ## 学生信息
            - 当前水平：%s
            - 学习科目：%s
            - 可用时间：%s
            - 学习目标：%s

            ## 计划要求
            请以JSON格式返回：
            {
              "dailyPlan": {
                "morning": "上午学习任务",
                "afternoon": "下午学习任务",
                "evening": "晚间复习任务"
              },
              "weeklyGoals": ["目标1", "目标2", "目标3"],
              "keyPoints": ["重点1", "重点2"],
              "practicePlan": "练习安排",
              "reviewPlan": "复习计划"
            }

            ## 计划原则
            1. 循序渐进，由浅入深
            2. 劳逸结合，避免疲劳
            3. 注重练习，及时巩固
            4. 定期复习，防止遗忘
            """, userLevel, targetSubject, availableTime, learningGoal);
    }
}
