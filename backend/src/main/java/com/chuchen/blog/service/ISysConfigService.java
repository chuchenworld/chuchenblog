package com.chuchen.blog.service;

public interface ISysConfigService {

    /**
     * 根据配置键获取配置值
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValueByKey(String configKey);

    /**
     * 更新配置
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 是否更新成功
     */
    boolean updateConfig(String configKey, String configValue);

    /**
     * 获取首页背景图URL
     * @return 背景图URL
     */
    String getHomeBackgroundUrl();

    /**
     * 更新首页背景图URL
     * @param url 背景图URL
     * @return 是否更新成功
     */
    boolean updateHomeBackgroundUrl(String url);

    /**
     * 获取留言板背景图URL
     * @return 背景图URL
     */
    String getMessagesBackgroundUrl();

    /**
     * 更新留言板背景图URL
     * @param url 背景图URL
     * @return 是否更新成功
     */
    boolean updateMessagesBackgroundUrl(String url);
}
