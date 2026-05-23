package com.flowershop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flowershop.entity.Cart;
import com.flowershop.entity.Product;
import com.flowershop.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService extends ServiceImpl<CartMapper, Cart> {

    @Autowired
    private ProductService productService;

    public List<Cart> getUserCart(Long userId) {
        List<Cart> cartList = lambdaQuery().eq(Cart::getUserId, userId).orderByDesc(Cart::getCreatedAt).list();
        for (Cart c : cartList) {
            Product p = productService.getById(c.getProductId());
            if (p != null) {
                c.setProductName(p.getName());
                c.setProductImage(p.getCoverImage());
                c.setPrice(p.getPrice());
                c.setDiscountPrice(p.getDiscountPrice());
                c.setStock(p.getStock());
            }
        }
        return cartList;
    }

    public Cart addCart(Long userId, Long productId, Integer quantity) {
        Cart exist = lambdaQuery().eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId).one();
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + quantity);
            updateById(exist);
            return exist;
        }
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cart.setSelected(1);
        save(cart);
        return cart;
    }

    public boolean updateQuantity(Long id, Long userId, Integer quantity) {
        return lambdaUpdate()
                .eq(Cart::getId, id)
                .eq(Cart::getUserId, userId)
                .set(Cart::getQuantity, quantity)
                .update();
    }

    public boolean toggleSelected(Long id, Long userId, Integer selected) {
        return lambdaUpdate()
                .eq(Cart::getId, id)
                .eq(Cart::getUserId, userId)
                .set(Cart::getSelected, selected)
                .update();
    }

    public boolean deleteCartItem(Long id, Long userId) {
        return lambdaUpdate()
                .eq(Cart::getId, id)
                .eq(Cart::getUserId, userId)
                .remove();
    }

    public List<Cart> getSelectedCartItems(Long userId) {
        List<Cart> cartList = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getSelected, 1)
                .list();
        for (Cart c : cartList) {
            Product p = productService.getById(c.getProductId());
            if (p != null) {
                c.setProductName(p.getName());
                c.setProductImage(p.getCoverImage());
                c.setPrice(p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice());
                c.setStock(p.getStock());
            }
        }
        return cartList;
    }

    public int getCartCount(Long userId) {
        Long count = lambdaQuery().eq(Cart::getUserId, userId).count();
        return count != null ? count.intValue() : 0;
    }

    public List<Cart> getByIds(List<Long> ids, Long userId) {
        List<Cart> cartList = lambdaQuery()
                .in(Cart::getId, ids)
                .eq(Cart::getUserId, userId)
                .list();
        for (Cart c : cartList) {
            Product p = productService.getById(c.getProductId());
            if (p != null) {
                c.setProductName(p.getName());
                c.setProductImage(p.getCoverImage());
                c.setPrice(p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice());
                c.setStock(p.getStock());
            }
        }
        return cartList;
    }

    public void selectAll(Long userId, Integer selected) {
        lambdaUpdate()
                .eq(Cart::getUserId, userId)
                .set(Cart::getSelected, selected)
                .update();
    }
}