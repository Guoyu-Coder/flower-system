package com.flowershop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flowershop.entity.Admin;
import com.flowershop.mapper.AdminMapper;
import org.springframework.stereotype.Service;

@Service
public class AdminService extends ServiceImpl<AdminMapper, Admin> {

    public Admin login(String username, String password) {
        return lambdaQuery()
                .eq(Admin::getUsername, username)
                .eq(Admin::getPassword, password)
                .eq(Admin::getStatus, 1)
                .one();
    }

    public Admin findByUsername(String username) {
        return lambdaQuery().eq(Admin::getUsername, username).one();
    }
}