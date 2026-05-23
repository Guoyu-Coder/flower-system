package com.flowershop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flowershop.entity.Category;
import com.flowershop.entity.Product;
import com.flowershop.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {

    @Autowired
    private CategoryService categoryService;
    
    // 预设的关联商品搭配（实际项目可以从数据库配置中读取）
    private static final List<Long> PACKAGE_CARD_IDS = Arrays.asList(10L, 11L, 12L); // 假设是贺卡类
    private static final List<Long> PACKAGE_WRAP_IDS = Arrays.asList(13L, 14L, 15L); // 包装类

    public Page<Product> getProductPage(int page, int size, Long categoryId, String keyword,
                                         BigDecimal minPrice, BigDecimal maxPrice, String sortBy) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getName, keyword)
                    .or().like(Product::getSubtitle, keyword)
                    .or().like(Product::getFlowerLanguage, keyword);
        }
        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }

        if ("sales".equals(sortBy)) {
            wrapper.orderByDesc(Product::getSales);
        } else if ("price_asc".equals(sortBy)) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            wrapper.orderByDesc(Product::getPrice);
        } else if ("new".equals(sortBy)) {
            wrapper.orderByDesc(Product::getCreatedAt);
        } else {
            wrapper.orderByDesc(Product::getSales);
        }

        Page<Product> pageResult = page(new Page<>(page, size), wrapper);
        for (Product p : pageResult.getRecords()) {
            Category c = categoryService.getById(p.getCategoryId());
            if (c != null) p.setCategoryName(c.getName());
        }
        return pageResult;
    }

    public List<Product> getHotProducts(int limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .eq(Product::getIsHot, 1)
                .orderByDesc(Product::getSales)
                .last("LIMIT " + limit);
        return list(wrapper);
    }

    public List<Product> getNewProducts(int limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .eq(Product::getIsNew, 1)
                .orderByDesc(Product::getCreatedAt)
                .last("LIMIT " + limit);
        return list(wrapper);
    }

    public List<Product> getRecommendByOccasion(String occasion, int limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .like(Product::getOccasion, occasion)
                .or().like(Product::getTags, occasion)
                .orderByDesc(Product::getSales)
                .last("LIMIT " + limit);
        return list(wrapper);
    }

    public List<Product> searchProducts(String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .like(Product::getName, keyword)
                .or().like(Product::getSubtitle, keyword)
                .or().like(Product::getDescription, keyword)
                .or().like(Product::getFlowerLanguage, keyword)
                .or().like(Product::getTags, keyword);
        return list(wrapper);
    }

    public long getCount() {
        return count();
    }

    public long getOnSaleCount() {
        return lambdaQuery().eq(Product::getStatus, 1).count();
    }

    /**
     * 获取商品的搭配推荐（智能购物篮）
     * @param productId 主商品ID
     * @return 推荐的搭配商品列表
     */
    public List<Product> getBundledRecommendations(Long productId) {
        List<Product> recommendations = new ArrayList<>();
        
        // 1. 先查找同分类或同场合的热门商品
        Product mainProduct = getById(productId);
        if (mainProduct == null) {
            return recommendations;
        }
        
        // 2. 查找贺卡、包装等附属商品（实际项目可以从数据库配置读取）
        // 这里简单演示：推荐热门的小配件
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales)
                .last("LIMIT 4");
        
        List<Product> allProducts = list(wrapper);
        for (Product p : allProducts) {
            if (!p.getId().equals(productId)) { // 排除自己
                // 如果是配饰类或便宜的小商品，优先推荐
                if (p.getPrice() != null && p.getPrice().compareTo(new BigDecimal("100")) < 0) {
                    recommendations.add(p);
                }
                if (recommendations.size() >= 2) {
                    break;
                }
            }
        }
        
        // 3. 如果没有找到配饰，推荐同分类的其他热门商品
        if (recommendations.isEmpty()) {
            for (Product p : allProducts) {
                if (!p.getId().equals(productId)) {
                    recommendations.add(p);
                    if (recommendations.size() >= 2) {
                        break;
                    }
                }
            }
        }
        
        return recommendations;
    }
}