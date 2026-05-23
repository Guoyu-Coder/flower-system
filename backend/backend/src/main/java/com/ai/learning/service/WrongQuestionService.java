package com.ai.learning.service;

import com.ai.learning.entity.WrongQuestion;
import com.ai.learning.mapper.WrongQuestionMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class WrongQuestionService extends ServiceImpl<WrongQuestionMapper, WrongQuestion> {

    public void addWrongQuestion(Long userId, Long questionId) {
        QueryWrapper<WrongQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("question_id", questionId);
        WrongQuestion wrongQuestion = this.getOne(queryWrapper);

        if (wrongQuestion == null) {
            wrongQuestion = new WrongQuestion();
            wrongQuestion.setUserId(userId);
            wrongQuestion.setQuestionId(questionId);
            wrongQuestion.setWrongCount(1);
            wrongQuestion.setMasteryLevel(1);
            this.save(wrongQuestion);
        } else {
            wrongQuestion.setWrongCount(wrongQuestion.getWrongCount() + 1);
            wrongQuestion.setMasteryLevel(1);
            this.updateById(wrongQuestion);
        }
    }

    public void updateMasteryLevel(Long userId, Long questionId, Integer masteryLevel) {
        QueryWrapper<WrongQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("question_id", questionId);
        WrongQuestion wrongQuestion = this.getOne(queryWrapper);

        if (wrongQuestion != null) {
            wrongQuestion.setMasteryLevel(masteryLevel);
            this.updateById(wrongQuestion);
        }
    }

    public Page<WrongQuestion> getWrongQuestions(Long userId, Integer page, Integer size) {
        Page<WrongQuestion> pageParam = new Page<>(page, size);
        QueryWrapper<WrongQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("last_wrong_time");
        return this.page(pageParam, queryWrapper);
    }

    public void removeFromWrongBook(Long userId, Long questionId) {
        QueryWrapper<WrongQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("question_id", questionId);
        this.remove(queryWrapper);
    }
}
