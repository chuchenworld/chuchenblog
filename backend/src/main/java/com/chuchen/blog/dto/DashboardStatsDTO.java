package com.chuchen.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 仪表盘统计数据DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    /**
     * 总文章数
     */
    private Long totalPosts;

    /**
     * 总访问量
     */
    private Long totalViews;

    /**
     * 用户数
     */
    private Long totalUsers;

    /**
     * 留言数
     */
    private Long totalComments;

    /**
     * 最近文章列表
     */
    private List<RecentPostDTO> recentPosts;

    /**
     * 最近文章DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentPostDTO {
        private Long id;
        private String title;
        private LocalDateTime createTime;
        private Long viewCount;
    }
}
