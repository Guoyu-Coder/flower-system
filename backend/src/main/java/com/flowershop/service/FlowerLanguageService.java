package com.flowershop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flowershop.entity.FlowerLanguage;
import com.flowershop.mapper.FlowerLanguageMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowerLanguageService extends ServiceImpl<FlowerLanguageMapper, FlowerLanguage> {

    public List<FlowerLanguage> searchByName(String name) {
        return lambdaQuery()
                .like(FlowerLanguage::getName, name)
                .or().like(FlowerLanguage::getAlias, name)
                .list();
    }

    public List<FlowerLanguage> searchByOccasion(String occasion) {
        return lambdaQuery()
                .like(FlowerLanguage::getOccasion, occasion)
                .or().like(FlowerLanguage::getMeaning, occasion)
                .or().like(FlowerLanguage::getTags, occasion)
                .list();
    }

    public List<FlowerLanguage> searchByKeyword(String keyword) {
        LambdaQueryWrapper<FlowerLanguage> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(FlowerLanguage::getName, keyword)
                .or().like(FlowerLanguage::getAlias, keyword)
                .or().like(FlowerLanguage::getMeaning, keyword)
                .or().like(FlowerLanguage::getFlowerLanguage, keyword)
                .or().like(FlowerLanguage::getOccasion, keyword)
                .or().like(FlowerLanguage::getTags, keyword);
        return list(wrapper);
    }

    public List<FlowerLanguage> getRecommendByBudget(String occasion, String budget) {
        LambdaQueryWrapper<FlowerLanguage> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(FlowerLanguage::getOccasion, occasion)
                .or().like(FlowerLanguage::getTags, occasion);
        return list(wrapper);
    }

    /**
     * 获取花语库总条数
     */
    public long getCount() {
        return count();
    }

    /**
     * 根据类别获取花语
     */
    public List<FlowerLanguage> getByCategory(String category) {
        return lambdaQuery().eq(FlowerLanguage::getCategory, category).list();
    }
}