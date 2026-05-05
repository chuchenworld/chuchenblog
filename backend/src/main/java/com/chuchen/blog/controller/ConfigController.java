package com.chuchen.blog.controller;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.SiteConfig;
import com.chuchen.blog.entity.User;
import com.chuchen.blog.service.SiteConfigService;
import com.chuchen.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {

    private final SiteConfigService siteConfigService;
    private final UserService userService;

    @GetMapping("/site/{configKey}")
    public Result<SiteConfig> getConfig(@PathVariable String configKey) {
        return siteConfigService.getConfig(configKey);
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping("/site/{configKey}")
    public Result<Void> updateConfig(
            @PathVariable String configKey,
            @RequestBody SiteConfig config) {
        return siteConfigService.updateConfig(configKey, config.getConfigValue());
    }

    @GetMapping("/admin/profile")
    public Result<User> getAdminProfile() {
        // 获取管理员用户信息（假设ID为1的用户是管理员）
        User admin = userService.getUserByUsername("admin");
        if (admin == null) {
            // 如果admin用户不存在，尝试获取第一个管理员用户
            admin = userService.getFirstAdmin();
        }
        if (admin != null) {
            admin.setPassword(null);
            return Result.success(admin);
        }
        return Result.error(404, "管理员用户不存在");
    }
}
