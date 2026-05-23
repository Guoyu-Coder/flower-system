package com.ai.learning.system.controller;

import com.ai.learning.common.result.Result;
import com.ai.learning.common.utils.JwtUtil;
import com.ai.learning.system.dto.LoginRequest;
import com.ai.learning.system.dto.LoginResponse;
import com.ai.learning.system.dto.RegisterRequest;
import com.ai.learning.system.entity.User;
import com.ai.learning.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@Api(tags = "用户管理")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getUsername(), request.getPassword(), request.getPhone());
        user.setPassword(null);
        return Result.success("注册成功", user);
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        Long userId = JwtUtil.getUserId(token);
        User user = userService.getUserInfo(userId);

        LoginResponse response = new LoginResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getRole()
        );

        return Result.success("登录成功", response);
    }

    @GetMapping("/info")
    @ApiOperation(value = "获取用户信息")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        User user = userService.getUserInfo(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PutMapping("/info")
    @ApiOperation(value = "修改用户信息")
    public Result<User> updateUserInfo(
            @RequestHeader("Authorization") String token,
            @RequestBody User user) {
        Long userId = JwtUtil.getUserId(token);
        user.setId(userId);
        User updatedUser = userService.updateUserInfo(user);
        updatedUser.setPassword(null);
        return Result.success("修改成功", updatedUser);
    }

    @PutMapping("/password")
    @ApiOperation(value = "修改密码")
    public Result<Void> updatePassword(
            @RequestHeader("Authorization") String token,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        Long userId = JwtUtil.getUserId(token);
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }

    @GetMapping("/check/username/{username}")
    @ApiOperation(value = "检查用户名是否存在")
    public Result<Boolean> checkUsername(@PathVariable String username) {
        return Result.success(userService.isUsernameExist(username));
    }

    @GetMapping("/check/phone/{phone}")
    @ApiOperation(value = "检查手机号是否存在")
    public Result<Boolean> checkPhone(@PathVariable String phone) {
        return Result.success(userService.isPhoneExist(phone));
    }
}
