package com.flowershop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 鲜花购物商城 - 主启动类
 */
@SpringBootApplication
public class FlowerShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowerShopApplication.class, args);
        System.out.println("✦✦✦ 鲜花购物商城系统启动成功 ✦✦✦");
        System.out.println("✦ 后端接口地址: http://localhost:8088");
        System.out.println("✦ Swagger文档: http://localhost:8088/swagger-ui.html");
        System.out.println("✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦");
    }
}