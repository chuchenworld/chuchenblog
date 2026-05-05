package com.chuchen.blog.service;

import com.chuchen.blog.dto.DashboardStatsDTO;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取仪表盘统计数据
     */
    DashboardStatsDTO getDashboardStats();
}
