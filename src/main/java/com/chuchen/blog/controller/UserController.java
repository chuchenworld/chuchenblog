package com.chuchen.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.dto.UserDTO;
import com.chuchen.blog.service.UserService;
import com.chuchen.blog.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('admin')")
    @GetMapping
    public Result<IPage<UserVO>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return userService.getUsers(page, size, keyword);
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public Result<Void> createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        return userService.resetPassword(id);
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
