package com.chuchen.blog.service.impl;

import com.chuchen.blog.dto.DashboardStatsDTO;
import com.chuchen.blog.entity.Comment;
import com.chuchen.blog.entity.Post;
import com.chuchen.blog.entity.User;
import com.chuchen.blog.mapper.CommentMapper;
import com.chuchen.blog.mapper.PostMapper;
import com.chuchen.blog.mapper.UserMapper;
import com.chuchen.blog.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 仪表盘服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        log.info("【DashboardServiceImpl】获取仪表盘统计数据");
        
        // 获取总文章数
        Long totalPosts = postMapper.selectCount(null);
        
        // 获取总访问量（所有文章的浏览量之和）
        Long totalViews = postMapper.selectList(null).stream()
                .mapToLong(post -> post.getViewCount() != null ? post.getViewCount() : 0)
                .sum();
        
        // 获取用户数
        Long totalUsers = userMapper.selectCount(null);
        
        // 获取留言数
        Long totalComments = commentMapper.selectCount(null);
        
        // 获取最近文章（按创建时间降序，取前5条）
        List<DashboardStatsDTO.RecentPostDTO> recentPosts = postMapper.selectList(null).stream()
                .sorted((a, b) -> {
                    if (a.getCreateTime() == null || b.getCreateTime() == null) {
                        return 0;
                    }
                    return b.getCreateTime().compareTo(a.getCreateTime());
                })
                .limit(5)
                .map(post -> DashboardStatsDTO.RecentPostDTO.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .createTime(post.getCreateTime())
                        .viewCount(post.getViewCount() != null ? post.getViewCount().longValue() : 0L)
                        .build())
                .collect(Collectors.toList());
        
        log.info("【DashboardServiceImpl】成功获取仪表盘统计数据 - 文章数: {}, 访问量: {}, 用户数: {}, 留言数: {}", 
                totalPosts, totalViews, totalUsers, totalComments);
        
        return DashboardStatsDTO.builder()
                .totalPosts(totalPosts)
                .totalViews(totalViews)
                .totalUsers(totalUsers)
                .totalComments(totalComments)
                .recentPosts(recentPosts)
                .build();
    }
}
