package com.flowershop.controller;

import com.flowershop.common.Result;
import com.flowershop.service.FavoriteService;
import com.flowershop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/toggle")
    public Result toggle(@RequestHeader(value = "Authorization", required = false) String token,
                        @RequestBody Map<String, Long> params) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        Long productId = params.get("productId");
        boolean isFav = favoriteService.toggleFavorite(userId, productId);
        return Result.success(isFav);
    }

    @GetMapping("/list")
    public Result list(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        return Result.success(favoriteService.getUserFavorites(userId));
    }

    @GetMapping("/check/{productId}")
    public Result check(@RequestHeader(value = "Authorization", required = false) String token,
                       @PathVariable Long productId) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        return Result.success(favoriteService.isFavorited(userId, productId));
    }

    private Long extractUserId(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return null;
            }
            return JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            return null;
        }
    }
}
