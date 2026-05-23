package com.flowershop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowershop.common.Result;
import com.flowershop.entity.Category;
import com.flowershop.entity.Product;
import com.flowershop.service.CategoryService;
import com.flowershop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "12") int size,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) BigDecimal minPrice,
                       @RequestParam(required = false) BigDecimal maxPrice,
                       @RequestParam(defaultValue = "sales") String sortBy) {
        Page<Product> productPage = productService.getProductPage(page, size, categoryId, keyword, minPrice, maxPrice, sortBy);
        return Result.success(productPage);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) return Result.error("商品不存在");
        Category category = categoryService.getById(product.getCategoryId());
        if (category != null) product.setCategoryName(category.getName());
        // 获取关联商品（同分类推荐）
        List<Product> related = productService.lambdaQuery()
                .eq(Product::getCategoryId, product.getCategoryId())
                .ne(Product::getId, product.getId())
                .eq(Product::getStatus, 1)
                .last("LIMIT 4")
                .list();
        product.setRelatedProducts(related);
        return Result.success(product);
    }

    @GetMapping("/hot")
    public Result hot(@RequestParam(defaultValue = "8") int limit) {
        return Result.success(productService.getHotProducts(limit));
    }

    @GetMapping("/new")
    public Result newProducts(@RequestParam(defaultValue = "8") int limit) {
        return Result.success(productService.getNewProducts(limit));
    }

    @GetMapping("/occasion/{occasion}")
    public Result occasion(@PathVariable String occasion,
                           @RequestParam(defaultValue = "8") int limit) {
        return Result.success(productService.getRecommendByOccasion(occasion, limit));
    }

    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        return Result.success(productService.searchProducts(keyword));
    }

    @GetMapping("/categories")
    public Result categories() {
        return Result.success(categoryService.getActiveCategories());
    }
}