package com.flowershop.controller;

import com.flowershop.common.Result;
import com.flowershop.entity.Category;
import com.flowershop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result list() {
        List<Category> categories = categoryService.getActiveCategories();
        return Result.success(categories);
    }
}
