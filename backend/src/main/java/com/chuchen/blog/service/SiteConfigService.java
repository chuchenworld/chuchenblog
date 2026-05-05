package com.chuchen.blog.service;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.SiteConfig;

public interface SiteConfigService {
    Result<SiteConfig> getConfig(String configKey);
    Result<Void> updateConfig(String configKey, String configValue);
}
