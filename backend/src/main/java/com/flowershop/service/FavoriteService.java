package com.flowershop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flowershop.entity.Favorite;
import com.flowershop.entity.Product;
import com.flowershop.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteService extends ServiceImpl<FavoriteMapper, Favorite> {

    @Autowired
    private ProductService productService;

    public boolean toggleFavorite(Long userId, Long productId) {
        Favorite exist = lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId)
                .one();
        if (exist != null) {
            return removeById(exist.getId());
        }
        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setProductId(productId);
        return save(fav);
    }

    public boolean isFavorited(Long userId, Long productId) {
        return lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId)
                .count() > 0;
    }

    public List<Product> getUserFavorites(Long userId) {
        List<Favorite> favs = lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreatedAt)
                .list();
        List<Product> products = new ArrayList<>();
        for (Favorite f : favs) {
            Product p = productService.getById(f.getProductId());
            if (p != null && p.getStatus() == 1) {
                products.add(p);
            }
        }
        return products;
    }

    public long getCount() {
        return count();
    }
}