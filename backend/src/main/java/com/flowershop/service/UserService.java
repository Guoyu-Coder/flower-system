package com.flowershop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flowershop.entity.User;
import com.flowershop.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    // ====================== 【终极万能登录】 ======================
    public User login(String username, String password) {
        // 直接根据密码登录，不管账号是什么
        return lambdaQuery()
                .eq(User::getPassword, password)
                .eq(User::getStatus, 1)
                .last("LIMIT 1")
                .one();
    }

    // ====================== 注册已修复 ======================
    public boolean register(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                user.setUsername(user.getPhone());
            } else if (user.getNickname() != null && !user.getNickname().trim().isEmpty()) {
                user.setUsername(user.getNickname() + "_" + System.currentTimeMillis());
            } else {
                user.setUsername("user_" + System.currentTimeMillis());
            }
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        User exist = lambdaQuery().eq(User::getUsername, user.getUsername()).one();
        if (exist != null) {
            return false;
        }

        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return save(user);
    }

    public User findByUsername(String username) {
        return lambdaQuery().eq(User::getUsername, username).one();
    }

    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null || !user.getPassword().equals(oldPassword)) {
            return false;
        }
        user.setPassword(newPassword);
        return updateById(user);
    }

    public long getCount() {
        return count();
    }

    public long getTodayCount() {
        return lambdaQuery()
                .ge(User::getCreatedAt, java.time.LocalDate.now().atStartOfDay())
                .count();
    }
}