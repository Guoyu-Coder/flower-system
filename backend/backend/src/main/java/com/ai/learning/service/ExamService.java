package com.ai.learning.service;

import com.ai.learning.entity.ExamRecord;
import com.ai.learning.entity.AnswerRecord;
import com.ai.learning.entity.QuestionBank;
import com.ai.learning.entity.WrongQuestion;
import com.ai.learning.mapper.ExamRecordMapper;
import com.ai.learning.mapper.AnswerRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamService extends ServiceImpl<ExamRecordMapper, ExamRecord> {

    @Autowired
    private AnswerRecordMapper answerRecordMapper;

    @Autowired
    private QuestionBankService questionBankService;

    @Autowired
    private WrongQuestionService wrongQuestionService;

    public ExamRecord createExam(String examTitle, Long userId, String subject, Integer totalScore, List<Long> questionIds) {
        ExamRecord exam = new ExamRecord();
        exam.setExamTitle(examTitle);
        exam.setUserId(userId);
        exam.setSubject(subject);
        exam.setTotalScore(totalScore);
        exam.setScore(0);
        exam.setStatus(0);
        exam.setStartTime(LocalDateTime.now());
        this.save(exam);
        return exam;
    }

    @Transactional
    public ExamRecord submitExam(Long examId, Long userId, List<AnswerRecord> answers) {
        ExamRecord exam = this.getById(examId);
        if (exam == null || !exam.getUserId().equals(userId)) {
            throw new RuntimeException("考试记录不存在");
        }

        int totalScore = 0;

        for (AnswerRecord answer : answers) {
            answer.setExamId(examId);
            QuestionBank question = questionBankService.getQuestionById(answer.getQuestionId());
            if (question == null) {
                continue;
            }

            boolean isCorrect = isAnswerCorrect(answer.getUserAnswer(), question.getAnswer(), question.getType());
            answer.setCorrectAnswer(question.getAnswer());
            answer.setIsCorrect(isCorrect ? 1 : 0);
            answer.setScore(isCorrect ? (100 / answers.size()) : 0);
            totalScore += answer.getScore();

            answerRecordMapper.insert(answer);

            if (!isCorrect) {
                wrongQuestionService.addWrongQuestion(userId, answer.getQuestionId());
            }
        }

        exam.setScore(totalScore);
        exam.setStatus(1);
        exam.setEndTime(LocalDateTime.now());
        this.updateById(exam);

        return exam;
    }

    private boolean isAnswerCorrect(String userAnswer, String correctAnswer, Integer type) {
        if (userAnswer == null || correctAnswer == null) {
            return false;
        }

        if (type == 1 || type == 2 || type == 3) {
            return userAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
        } else {
            return userAnswer.trim().equals(correctAnswer.trim());
        }
    }

    public Page<ExamRecord> getExamList(Long userId, Integer page, Integer size) {
        Page<ExamRecord> pageParam = new Page<>(page, size);
        QueryWrapper<ExamRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        return this.page(pageParam, queryWrapper);
    }

    public ExamRecord getExamDetail(Long examId, Long userId) {
        ExamRecord exam = this.getById(examId);
        if (exam == null || !exam.getUserId().equals(userId)) {
            throw new RuntimeException("考试记录不存在");
        }

        QueryWrapper<AnswerRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exam_id", examId);
        exam.set("answer_records", answerRecordMapper.selectList(queryWrapper));

        return exam;
    }
}
