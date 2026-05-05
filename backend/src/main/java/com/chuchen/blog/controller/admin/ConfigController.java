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

    @Operation(summary = "获取留言板背景图配置")
    @GetMapping("/messages-bg")
    public Result<String> getMessagesBackground() {
        String url = sysConfigService.getMessagesBackgroundUrl();
        return Result.success(url);
    }

    @Operation(summary = "更新留言板背景图配置")
    @PostMapping("/messages-bg")
    public Result<Void> updateMessagesBackground(@RequestBody(required = false) String url) {
        boolean updated = sysConfigService.updateMessagesBackgroundUrl(url != null ? url : "");
        if (updated) {
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }

    @Operation(summary = "获取配置")
    @GetMapping("/{configKey}")
    public Result<?> getConfig(@PathVariable String configKey) {
        String value = sysConfigService.getConfigValueByKey(configKey);
        return Result.success(new ConfigResponse(configKey, value));
    }

    @Operation(summary = "更新配置")
    @PostMapping("/{configKey}")
    public Result<Void> updateConfig(@PathVariable String configKey, @RequestBody ConfigRequest request) {
        boolean updated = sysConfigService.updateConfig(configKey, request.getConfigValue());
        if (updated) {
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }

    public static class ConfigRequest {
        private String configValue;

        public String getConfigValue() {
            return configValue;
        }

        public void setConfigValue(String configValue) {
            this.configValue = configValue;
        }
    }

    public static class ConfigResponse {
        private String configKey;
        private String configValue;

        public ConfigResponse(String configKey, String configValue) {
            this.configKey = configKey;
            this.configValue = configValue;
        }

        public String getConfigKey() {
            return configKey;
        }

        public void setConfigKey(String configKey) {
            this.configKey = configKey;
        }

        public String getConfigValue() {
            return configValue;
        }

        public void setConfigValue(String configValue) {
            this.configValue = configValue;
        }
    }
}
