package com.ai.learning.service;

import com.ai.learning.entity.QuestionBank;
import com.ai.learning.mapper.QuestionBankMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionBankService extends ServiceImpl<QuestionBankMapper, QuestionBank> {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Page<QuestionBank> getQuestionList(Integer page, Integer size, String subject, Integer type, Integer difficulty) {
        Page<QuestionBank> pageParam = new Page<>(page, size);
        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();

        if (subject != null && !subject.isEmpty()) {
            queryWrapper.eq("subject", subject);
        }
        if (type != null) {
            queryWrapper.eq("type", type);
        }
        if (difficulty != null) {
            queryWrapper.eq("difficulty", difficulty);
        }

        queryWrapper.orderByDesc("create_time");
        return this.page(pageParam, queryWrapper);
    }

    public QuestionBank getQuestionById(Long id) {
        String cacheKey = "question:" + id;
        String cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            return com.alibaba.fastjson.JSON.parseObject(cached, QuestionBank.class);
        }

        QuestionBank question = this.getById(id);
        if (question != null) {
            redisTemplate.opsForValue().set(cacheKey, com.alibaba.fastjson.JSON.toJSONString(question), 24, TimeUnit.HOURS);
        }
        return question;
    }

    public QuestionBank createQuestion(QuestionBank question, Long creatorId) {
        question.setCreatorId(creatorId);
        question.setUsageCount(0);
        this.save(question);
        return question;
    }

    public void incrementUsageCount(Long questionId) {
        QuestionBank question = this.getById(questionId);
        if (question != null) {
            question.setUsageCount(question.getUsageCount() + 1);
            this.updateById(question);
        }
    }

    public List<QuestionBank> getHotQuestions(String subject, Integer limit) {
        String cacheKey = "hot:questions:" + subject;
        String cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            return com.alibaba.fastjson.JSON.parseArray(cached, QuestionBank.class);
        }

        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
        if (subject != null && !subject.isEmpty()) {
            queryWrapper.eq("subject", subject);
        }
        queryWrapper.orderByDesc("usage_count").last("LIMIT " + limit);

        List<QuestionBank> questions = this.list(queryWrapper);
        redisTemplate.opsForValue().set(cacheKey, com.alibaba.fastjson.JSON.toJSONString(questions), 1, TimeUnit.HOURS);

        return questions;
    }
}
