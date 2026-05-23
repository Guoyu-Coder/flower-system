package com.ai.learning.system.entity;

import com.ai.learning.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    private String username;
    
    private String password;
    
    private String realName;
    
    private String phone;
    
    private String email;
    
    private String avatar;
    
    private String role;
    
    private Integer status;
    
    private Integer totalScore;
    
    private Long totalDuration;
    
    private Integer totalQuestions;
    
    private Integer totalCorrect;
    
    private String lastLoginTime;
    
    private String lastLoginIp;
}
