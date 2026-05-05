package com.chuchen.blog.controller.admin;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.dto.DashboardStatsDTO;
import com.chuchen.blog.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping
    public Result<DashboardStatsDTO> getDashboardStats() {
        log.info("【DashboardController】获取仪表盘统计数据");
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("【DashboardController】获取仪表盘统计数据失败 - 错误类型: {}, 错误信息: {}", 
                    e.getClass().getName(), e.getMessage(), e);
            return Result.error("获取仪表盘数据失败: " + e.getMessage());
        }
    }
}
