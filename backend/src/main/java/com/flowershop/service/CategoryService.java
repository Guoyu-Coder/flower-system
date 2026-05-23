package com.flowershop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flowershop.entity.Category;
import com.flowershop.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    public List<Category> getActiveCategories() {
        return lambdaQuery().eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder)
                .list();
    }
}