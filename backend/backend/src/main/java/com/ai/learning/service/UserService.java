package com.ai.learning.service;

import com.ai.learning.entity.User;
import com.ai.learning.mapper.UserMapper;
import com.ai.learning.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public User register(String username, String password, String realName, String email, String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if (this.count(queryWrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encrypt(password));
        user.setRealName(realName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(1);
        user.setStatus(1);
        this.save(user);
        return user;
    }

    public User login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = this.getOne(queryWrapper);

        if (user == null || !PasswordUtil.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        return user;
    }

    public void saveToken(Long userId, String token) {
        redisTemplate.opsForValue().set("token:" + userId, token, 2, TimeUnit.HOURS);
    }

    public void removeToken(Long userId) {
        redisTemplate.delete("token:" + userId);
    }

    public User getUserById(Long userId) {
        return this.getById(userId);
    }
}
