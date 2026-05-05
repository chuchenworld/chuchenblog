package com.chuchen.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.dto.LoginRequest;
import com.chuchen.blog.dto.LoginResponse;
import com.chuchen.blog.entity.User;
import com.chuchen.blog.mapper.UserMapper;
import com.chuchen.blog.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public Result<LoginResponse> login(LoginRequest request) {
        System.out.println("Login request: account=" + request.getAccount() + ", password=" + request.getPassword());
        
        // 构建查询条件，支持手机号或邮箱登录
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (request.getAccount().contains("@")) {
            // 邮箱格式
            queryWrapper.eq(User::getUsername, request.getAccount());
        } else {
            // 手机号格式
            queryWrapper.eq(User::getUsername, request.getAccount());
        }
        
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            System.out.println("User not found: " + request.getAccount());
            return Result.error(401, "账号或密码错误");
        }

        System.out.println("Found user: " + user.getUsername() + ", password hash: " + user.getPassword());
        
        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("Password match: " + passwordMatch);
        
        if (!passwordMatch) {
            return Result.error(401, "账号或密码错误");
        }

        if (user.getStatus() != 1) {
            return Result.error(403, "用户已被禁用");
        }

        String token = jwtUtils.generateToken(user.getUsername());
        return Result.success(new LoginResponse(token, user.getUsername(), user.getNickname(), user.getRole()));
    }
}