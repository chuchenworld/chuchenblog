package com.chuchen.blog.service;

import com.chuchen.blog.entity.FriendLink;

import java.util.List;

/**
 * 友情链接服务接口
 */
public interface FriendLinkService {

    /**
     * 获取所有已通过审核的友链（前端展示用）
     */
    List<FriendLink> getApprovedLinks();

    /**
     * 获取所有友链（管理员管理用）
     */
    List<FriendLink> getAllLinks();

    /**
     * 创建友链申请
     */
    FriendLink createFriendLink(FriendLink friendLink);

    /**
     * 更新友链状态
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 删除友链
     */
    boolean deleteFriendLink(Long id);

    /**
     * 根据ID获取友链
     */
    FriendLink getById(Long id);
}