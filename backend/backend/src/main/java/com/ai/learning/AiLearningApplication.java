package com.ai.learning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ai.learning.mapper")
public class AiLearningApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiLearningApplication.class, args);
    }
}
