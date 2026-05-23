package com.flowershop.controller;

import com.flowershop.common.Result;
import com.flowershop.entity.User;
import com.flowershop.service.UserService;
import com.flowershop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            boolean success = userService.register(user);
            if (!success) {
                return Result.error("用户名已存在");
            }
            return Result.success("注册成功");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        User user = userService.login(username, password);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        String token = JwtUtils.generateToken(user.getId(), user.getUsername());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.success(data);
    }

    @GetMapping("/info")
    public Result getInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.trim().isEmpty()) {
            return Result.unauthorized();
        }

        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.unauthorized();
            }

            Long userId = JwtUtils.getUserId(actualToken);
            if (userId == null) {
                return Result.unauthorized();
            }

            User user = userService.getById(userId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.unauthorized();
        }
    }

    @PutMapping("/update")
    public Result updateInfo(@RequestHeader(value = "Authorization", required = false) String token,
                           @RequestBody User user) {
        if (token == null || token.trim().isEmpty()) {
            return Result.unauthorized();
        }

        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.unauthorized();
            }

            Long userId = JwtUtils.getUserId(actualToken);
            if (userId == null) {
                return Result.unauthorized();
            }

            user.setId(userId);
            userService.updateById(user);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.unauthorized();
        }
    }

    @PutMapping("/password")
    public Result updatePassword(@RequestHeader(value = "Authorization", required = false) String token,
                               @RequestBody Map<String, String> params) {
        if (token == null || token.trim().isEmpty()) {
            return Result.unauthorized();
        }

        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return Result.unauthorized();
            }

            Long userId = JwtUtils.getUserId(actualToken);
            if (userId == null) {
                return Result.unauthorized();
            }

            boolean success = userService.updatePassword(userId, params.get("oldPassword"), params.get("newPassword"));
            if (!success) return Result.error("原密码错误");
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.unauthorized();
        }
    }
}
