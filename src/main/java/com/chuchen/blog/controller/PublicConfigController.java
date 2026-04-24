package com.chuchen.blog.controller;

import com.chuchen.blog.service.ISysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/home-bg")
    public ResponseEntity<String> getHomeBackground() {
        String url = sysConfigService.getHomeBackgroundUrl();
        return ResponseEntity.ok(url);
    }
}
