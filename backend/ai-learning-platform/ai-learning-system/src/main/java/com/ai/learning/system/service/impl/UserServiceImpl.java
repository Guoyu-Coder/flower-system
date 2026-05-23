package com.ai.learning.system.service.impl;

import com.ai.learning.common.constant.RedisConstant;
import com.ai.learning.common.constant.UserConstant;
import com.ai.learning.common.exception.BusinessException;
import com.ai.learning.common.utils.JwtUtil;
import com.ai.learning.common.utils.PasswordUtil;
import com.ai.learning.common.utils.RedisUtil;
import com.ai.learning.system.entity.User;
import com.ai.learning.system.mapper.UserMapper;
import com.ai.learning.system.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User register(String username, String password, String phone) {
        if (isUsernameExist(username)) {
            throw BusinessException.of("用户名已存在");
        }
        
        if (phone != null && isPhoneExist(phone)) {
            throw BusinessException.of("手机号已被注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encrypt(password));
        user.setPhone(phone);
        user.setRole(UserConstant.ROLE_STUDENT);
        user.setStatus(1);
        user.setTotalScore(0);
        user.setTotalDuration(0L);
        user.setTotalQuestions(0);
        user.setTotalCorrect(0);

        userMapper.insert(user);
        log.info("用户注册成功: {}", username);
        return user;
    }

    @Override
    public String login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username).or().eq(User::getPhone, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw BusinessException.of(401, "用户不存在");
        }

        if (user.getStatus() == 0) {
            throw BusinessException.of(401, "账号已被禁用");
        }

        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw BusinessException.of(401, "密码错误");
        }

        String token = JwtUtil.createToken(user.getId(), user.getUsername(), user.getRole());
        
        RedisUtil.set(RedisConstant.USER_TOKEN_PREFIX + token, user.getId(), 
            RedisConstant.TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        
        log.info("用户登录成功: {}", username);
        return token;
    }

    @Override
    public User getUserInfo(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User updateUserInfo(User user) {
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw BusinessException.of("用户不存在");
        }

        if (user.getRealName() != null) {
            existUser.setRealName(user.getRealName());
        }
        if (user.getEmail() != null) {
            existUser.setEmail(user.getEmail());
        }
        if (user.getPhone() != null) {
            existUser.setPhone(user.getPhone());
        }
        if (user.getAvatar() != null) {
            existUser.setAvatar(user.getAvatar());
        }

        userMapper.updateById(existUser);
        return existUser;
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.of("用户不存在");
        }

        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw BusinessException.of("原密码错误");
        }

        user.setPassword(PasswordUtil.encrypt(newPassword));
        userMapper.updateById(user);
        log.info("用户修改密码成功: {}", user.getUsername());
    }

    @Override
    public boolean isUsernameExist(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean isPhoneExist(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return userMapper.selectCount(wrapper) > 0;
    }
}
