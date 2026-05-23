package com.ai.learning.system.service;

import com.ai.learning.system.entity.User;

public interface UserService {

    User register(String username, String password, String phone);
    
    String login(String username, String password);
    
    User getUserInfo(Long userId);
    
    User updateUserInfo(User user);
    
    void updatePassword(Long userId, String oldPassword, String newPassword);
    
    boolean isUsernameExist(String username);
    
    boolean isPhoneExist(String phone);
}
