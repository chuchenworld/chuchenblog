package com.chuchen.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.chuchen.blog.mapper")
@EnableAsync
public class ChuchenBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChuchenBlogApplication.class, args);
    }
}