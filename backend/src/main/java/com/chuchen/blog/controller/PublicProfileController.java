package com.chuchen.blog.controller;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.User;
import com.chuchen.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class PublicProfileController {

    private final UserService userService;

    @Operation(summary = "获取博主公开资料（用于前台展示）")
    @GetMapping
    public Result<User> getPublicProfile() {
        User user = userService.getUserByRole("admin");
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(null);
        user.setEmail(null);
        user.setUsername(null);
        return Result.success(user);
    }
}