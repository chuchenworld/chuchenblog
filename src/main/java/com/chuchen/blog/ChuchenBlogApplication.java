package com.chuchen.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chuchen.blog.mapper")
public class ChuchenBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChuchenBlogApplication.class, args);
    }
}