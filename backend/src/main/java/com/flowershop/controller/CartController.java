package com.flowershop.controller;

import com.flowershop.common.Result;
import com.flowershop.entity.Cart;
import com.flowershop.service.CartService;
import com.flowershop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/list")
    public Result list(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.trim().isEmpty()) {
            return Result.error("请先登录");
        }

        Long userId = null;
        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.error("请先登录");
            }
            userId = JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            return Result.error("登录已过期，请重新登录");
        }

        if (userId == null) {
            return Result.error("请先登录");
        }
        return Result.success(cartService.getUserCart(userId));
    }

    @PostMapping("/add")
    public Result add(@RequestHeader(value = "Authorization", required = false) String token,
                     @RequestBody Map<String, Object> params) {
        if (token == null || token.trim().isEmpty()) {
            return Result.error("请先登录");
        }

        Long userId = null;
        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.error("请先登录");
            }
            userId = JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            return Result.error("登录已过期，请重新登录");
        }

        if (userId == null) {
            return Result.error("请先登录");
        }

        Long productId = Long.valueOf(params.get("productId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        Cart cart = cartService.addCart(userId, productId, quantity);
        return Result.success(cart);
    }

    @PutMapping("/quantity")
    public Result updateQuantity(@RequestHeader(value = "Authorization", required = false) String token,
                                @RequestBody Map<String, Object> params) {
        if (token == null || token.trim().isEmpty()) {
            return Result.error("请先登录");
        }

        Long userId = null;
        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.error("请先登录");
            }
            userId = JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            return Result.error("登录已过期，请重新登录");
        }

        if (userId == null) {
            return Result.error("请先登录");
        }

        Long id = Long.valueOf(params.get("id").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        cartService.updateQuantity(id, userId, quantity);
        return Result.success("修改成功");
    }

    @PutMapping("/selected")
    public Result toggleSelected(@RequestHeader(value = "Authorization", required = false) String token,
                                @RequestBody Map<String, Object> params) {
        if (token == null || token.trim().isEmpty()) {
            return Result.error("请先登录");
        }

        Long userId = null;
        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.error("请先登录");
            }
            userId = JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            return Result.error("登录已过期，请重新登录");
        }

        if (userId == null) {
            return Result.error("请先登录");
        }

        Long id = Long.valueOf(params.get("id").toString());
        Integer selected = Integer.valueOf(params.get("selected").toString());
        cartService.toggleSelected(id, userId, selected);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@RequestHeader(value = "Authorization", required = false) String token,
                        @PathVariable Long id) {
        if (token == null || token.trim().isEmpty()) {
            return Result.error("请先登录");
        }

        Long userId = null;
        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.error("请先登录");
            }
            userId = JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            return Result.error("登录已过期，请重新登录");
        }

        if (userId == null) {
            return Result.error("请先登录");
        }

        cartService.deleteCartItem(id, userId);
        return Result.success("删除成功");
    }

    @GetMapping("/selected")
    public Result selected(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.trim().isEmpty()) {
            return Result.error("请先登录");
        }

        Long userId = null;
        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.error("请先登录");
            }
            userId = JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            return Result.error("登录已过期，请重新登录");
        }

        if (userId == null) {
            return Result.error("请先登录");
        }

        return Result.success(cartService.getSelectedCartItems(userId));
    }

    @GetMapping("/batch")
    public Result getByIds(@RequestHeader(value = "Authorization", required = false) String token,
                          @RequestParam("ids") String idsStr) {
        if (token == null || token.trim().isEmpty()) {
            return Result.error("请先登录");
        }

        Long userId = null;
        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.error("请先登录");
            }
            userId = JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            return Result.error("登录已过期，请重新登录");
        }

        if (userId == null) {
            return Result.error("请先登录");
        }

        try {
            String[] idArr = idsStr.split(",");
            List<Long> ids = new ArrayList<>();
            for (String id : idArr) {
                ids.add(Long.parseLong(id.trim()));
            }
            return Result.success(cartService.getByIds(ids, userId));
        } catch (Exception e) {
            return Result.error("参数错误");
        }
    }

    @PutMapping("/selectAll")
    public Result selectAll(@RequestHeader(value = "Authorization", required = false) String token,
                           @RequestParam Boolean selected) {
        if (token == null || token.trim().isEmpty()) {
            return Result.error("请先登录");
        }

        Long userId = null;
        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.error("请先登录");
            }
            userId = JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            return Result.error("登录已过期，请重新登录");
        }

        if (userId == null) {
            return Result.error("请先登录");
        }

        cartService.selectAll(userId, selected ? 1 : 0);
        return Result.success("修改成功");
    }

    @GetMapping("/count")
    public Result count(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.trim().isEmpty()) {
            return Result.success(0);
        }

        Long userId = null;
        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.success(0);
            }
            userId = JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            return Result.success(0);
        }

        if (userId == null) {
            return Result.success(0);
        }

        return Result.success(cartService.getCartCount(userId));
    }
}
