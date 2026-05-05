package com.chuchen.blog.controller.admin;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.User;
import com.chuchen.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "个人资料管理")
public class ProfileController {

    private final UserService userService;

    @Operation(summary = "获取个人资料")
    @GetMapping
    public Result<User> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Operation(summary = "更新个人资料")
    @PutMapping
    public Result<Void> updateProfile(@RequestBody User userUpdate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        if (userUpdate.getNickname() != null) {
            user.setNickname(userUpdate.getNickname());
        }
        if (userUpdate.getAvatar() != null) {
            user.setAvatar(userUpdate.getAvatar());
        }
        if (userUpdate.getBio() != null) {
            user.setBio(userUpdate.getBio());
        }
        if (userUpdate.getEmail() != null) {
            user.setEmail(userUpdate.getEmail());
        }
        if (userUpdate.getTags() != null) {
            user.setTags(userUpdate.getTags());
        }

        userService.updateUser(user);
        return Result.success();
    }
}