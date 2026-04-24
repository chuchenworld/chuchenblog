package com.chuchen.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chuchen.blog.entity.SysConfig;
import com.chuchen.blog.mapper.SysConfigMapper;
import com.chuchen.blog.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysConfigServiceImpl implements ISysConfigService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public String getConfigValueByKey(String configKey) {
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysConfig::getConfigKey, configKey);
        SysConfig sysConfig = sysConfigMapper.selectOne(queryWrapper);
        return sysConfig != null ? sysConfig.getConfigValue() : null;
    }

    @Override
    public boolean updateConfig(String configKey, String configValue) {
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysConfig::getConfigKey, configKey);
        SysConfig sysConfig = sysConfigMapper.selectOne(queryWrapper);
        if (sysConfig != null) {
            sysConfig.setConfigValue(configValue);
            return sysConfigMapper.updateById(sysConfig) > 0;
        }
        return false;
    }

    @Override
    public String getHomeBackgroundUrl() {
        return getConfigValueByKey("home_background_url");
    }

    @Override
    public boolean updateHomeBackgroundUrl(String url) {
        return updateConfig("home_background_url", url);
    }
}
