package com.chuchen.blog.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 测试密码
        String password = "password";
        
        // 数据库中的密码
        String dbPassword = "$2a$10$RxZaYVu1fbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW";
        
        // 验证密码
        boolean matches = encoder.matches(password, dbPassword);
        System.out.println("密码验证结果: " + matches);
        
        // 生成新的密码
        String newPassword = encoder.encode(password);
        System.out.println("新生成的密码: " + newPassword);
    }
}
