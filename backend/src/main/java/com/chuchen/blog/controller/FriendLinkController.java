package com.chuchen.blog.controller;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.FriendLink;
import com.chuchen.blog.service.FriendLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 友情链接控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/friend-links")
@RequiredArgsConstructor
public class FriendLinkController {

    private final FriendLinkService friendLinkService;

    /**
     * 获取所有已通过审核的友链（前端展示用）
     */
    @GetMapping
    public Result<List<FriendLink>> getApprovedLinks() {
        log.info("【FriendLinkController】获取已通过审核的友链");
        try {
            List<FriendLink> links = friendLinkService.getApprovedLinks();
            log.info("【FriendLinkController】成功获取 {} 条已通过审核的友链", links.size());
            return Result.success(links);
        } catch (Exception e) {
            log.error("【FriendLinkController】获取已通过审核的友链失败 - 错误类型: {}, 错误信息: {}", 
                e.getClass().getName(), e.getMessage(), e);
            return Result.error("获取友链列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有友链（管理员管理用）
     */
    @GetMapping("/admin")
    public Result<List<FriendLink>> getAllLinks() {
        log.info("【FriendLinkController】获取所有友链（管理员）");
        try {
            List<FriendLink> links = friendLinkService.getAllLinks();
            log.info("【FriendLinkController】成功获取 {} 条友链", links.size());
            return Result.success(links);
        } catch (Exception e) {
            log.error("【FriendLinkController】获取所有友链失败 - 错误类型: {}, 错误信息: {}", 
                e.getClass().getName(), e.getMessage(), e);
            return Result.error("获取友链列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建友链申请
     */
    @PostMapping
    public Result<FriendLink> createFriendLink(@RequestBody FriendLink friendLink) {
        log.info("【FriendLinkController】创建友链申请 - 站点名称: {}", friendLink.getSiteName());
        
        if (friendLink.getSiteName() == null || friendLink.getSiteName().trim().isEmpty()) {
            return Result.error("站点名称不能为空");
        }
        if (friendLink.getSiteUrl() == null || friendLink.getSiteUrl().trim().isEmpty()) {
            return Result.error("站点链接不能为空");
        }
        
        try {
            FriendLink created = friendLinkService.createFriendLink(friendLink);
            log.info("【FriendLinkController】友链申请创建成功，ID: {}", created.getId());
            return Result.success(created, "申请提交成功，等待审核");
        } catch (Exception e) {
            log.error("【FriendLinkController】创建友链申请失败 - 站点名称: {}, 错误类型: {}, 错误信息: {}", 
                friendLink.getSiteName(), e.getClass().getName(), e.getMessage(), e);
            return Result.error("创建友链申请失败: " + e.getMessage());
        }
    }

    /**
     * 更新友链状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody FriendLink request) {
        log.info("【FriendLinkController】更新友链状态 - ID: {}, 状态: {}", id, request.getStatus());
        
        try {
            boolean success = friendLinkService.updateStatus(id, request.getStatus());
            if (success) {
                String message = request.getStatus() == 1 ? "审核通过" : "状态更新成功";
                log.info("【FriendLinkController】友链状态更新成功 - ID: {}, 新状态: {}", id, request.getStatus());
                return Result.success(null, message);
            }
            return Result.error("友链不存在");
        } catch (Exception e) {
            log.error("【FriendLinkController】更新友链状态失败 - ID: {}, 状态: {}, 错误类型: {}, 错误信息: {}", 
                id, request.getStatus(), e.getClass().getName(), e.getMessage(), e);
            return Result.error("更新友链状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除友链
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFriendLink(@PathVariable Long id) {
        log.info("【FriendLinkController】删除友链 - ID: {}", id);
        
        try {
            boolean success = friendLinkService.deleteFriendLink(id);
            if (success) {
                log.info("【FriendLinkController】友链删除成功 - ID: {}", id);
                return Result.success(null, "删除成功");
            }
            return Result.error("友链不存在");
        } catch (Exception e) {
            log.error("【FriendLinkController】删除友链失败 - ID: {}, 错误类型: {}, 错误信息: {}", 
                id, e.getClass().getName(), e.getMessage(), e);
            return Result.error("删除友链失败: " + e.getMessage());
        }
    }
}