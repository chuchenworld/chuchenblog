package com.chuchen.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.SiteConfig;
import com.chuchen.blog.mapper.SiteConfigMapper;
import com.chuchen.blog.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SiteConfigServiceImpl implements SiteConfigService {

    private final SiteConfigMapper siteConfigMapper;

    @Override
    public Result<SiteConfig> getConfig(String configKey) {
        LambdaQueryWrapper<SiteConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteConfig::getConfigKey, configKey);
        SiteConfig config = siteConfigMapper.selectOne(queryWrapper);
        
        if (config == null) {
            return Result.error(404, "配置不存在");
        }
        
        return Result.success(config);
    }

    @Override
    public Result<Void> updateConfig(String configKey, String configValue) {
        LambdaQueryWrapper<SiteConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteConfig::getConfigKey, configKey);
        SiteConfig config = siteConfigMapper.selectOne(queryWrapper);
        
        if (config == null) {
            return Result.error(404, "配置不存在");
        }
        
        config.setConfigValue(configValue);
        config.setUpdateTime(LocalDateTime.now());
        siteConfigMapper.updateById(config);
        
        return Result.success();
    }
}
