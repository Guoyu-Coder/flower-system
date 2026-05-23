package com.ai.learning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.ai.learning.**.mapper")
@ComponentScan(basePackages = {"com.ai.learning", "com.ai.learning.agent"})
public class AiLearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLearningApplication.class, args);
        System.out.println("");
        System.out.println("========================================");
        System.out.println("  智学星途 - AI智能学习平台");
        System.out.println("  Smart Learning Journey");
        System.out.println("  服务启动成功！");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("  API文档: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================");
        System.out.println("");
    }
}
