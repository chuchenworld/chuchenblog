package com.chuchen.blog.controller.admin;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.service.ISysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminConfigController")
@RequestMapping("/api/admin/config")
@Tag(name = "Config", description = "配置管理")
public class ConfigController {

    @Autowired
    private ISysConfigService sysConfigService;

    @Operation(summary = "获取首页背景图配置")
    @GetMapping("/home-bg")
    public Result<String> getHomeBackground() {
        String url = sysConfigService.getHomeBackgroundUrl();
        return Result.success(url);
    }

    @Operation(summary = "更新首页背景图配置")
    @PostMapping("/home-bg")
    public Result<Void> updateHomeBackground(@RequestBody String url) {
        boolean updated = sysConfigService.updateHomeBackgroundUrl(url);
        if (updated) {
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }
}
