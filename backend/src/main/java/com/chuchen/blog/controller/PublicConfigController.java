package com.chuchen.blog.controller;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.service.ISysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
@Tag(name = "PublicConfig", description = "公开配置")
public class PublicConfigController {

    @Autowired
    private ISysConfigService sysConfigService;

    @Operation(summary = "获取首页背景图配置")
    @GetMapping("/home_bg")
    public Result<String> getHomeBackground() {
        String url = sysConfigService.getHomeBackgroundUrl();
        return Result.success(url != null ? url : "");
    }

    @Operation(summary = "获取首页标语配置")
    @GetMapping("/home_slogan")
    public Result<String> getHomeSlogan() {
        String slogan = sysConfigService.getConfigValueByKey("home_slogan");
        return Result.success(slogan != null ? slogan : "");
    }

    @Operation(summary = "获取留言板背景图配置")
    @GetMapping("/messages_bg")
    public Result<String> getMessagesBackground() {
        String url = sysConfigService.getMessagesBackgroundUrl();
        return Result.success(url != null ? url : "");
    }
}
