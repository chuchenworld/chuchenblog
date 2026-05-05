package com.chuchen.blog.service.impl;

import com.chuchen.blog.entity.FriendLink;
import com.chuchen.blog.event.FriendLinkCreatedEvent;
import com.chuchen.blog.mapper.FriendLinkMapper;
import com.chuchen.blog.service.FriendLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 友情链接服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FriendLinkServiceImpl implements FriendLinkService {

    private final FriendLinkMapper friendLinkMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<FriendLink> getApprovedLinks() {
        log.info("【FriendLinkServiceImpl】查询已通过审核的友链");
        return friendLinkMapper.selectApprovedLinks();
    }

    @Override
    public List<FriendLink> getAllLinks() {
        log.info("【FriendLinkServiceImpl】查询所有友链");
        return friendLinkMapper.selectAllLinks();
    }

    @Override
    @Transactional
    public FriendLink createFriendLink(FriendLink friendLink) {
        log.info("【FriendLinkServiceImpl】创建友链申请 - 站点名称: {}", friendLink.getSiteName());
        
        friendLink.setStatus(0); // 默认待审核
        friendLinkMapper.insert(friendLink);
        
        log.info("【FriendLinkServiceImpl】友链申请创建成功，ID: {}", friendLink.getId());
        
        // 发布友链创建事件，触发邮件通知
        eventPublisher.publishEvent(new FriendLinkCreatedEvent(friendLink));
        
        return friendLink;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        log.info("【FriendLinkServiceImpl】更新友链状态 - ID: {}, 状态: {}", id, status);
        
        FriendLink friendLink = friendLinkMapper.selectById(id);
        if (friendLink == null) {
            log.warn("【FriendLinkServiceImpl】友链不存在 - ID: {}", id);
            return false;
        }
        
        friendLink.setStatus(status);
        friendLinkMapper.updateById(friendLink);
        
        log.info("【FriendLinkServiceImpl】友链状态更新成功 - ID: {}", id);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteFriendLink(Long id) {
        log.info("【FriendLinkServiceImpl】删除友链 - ID: {}", id);
        
        FriendLink friendLink = friendLinkMapper.selectById(id);
        if (friendLink == null) {
            log.warn("【FriendLinkServiceImpl】友链不存在 - ID: {}", id);
            return false;
        }
        
        friendLinkMapper.deleteById(id);
        
        log.info("【FriendLinkServiceImpl】友链删除成功 - ID: {}", id);
        return true;
    }

    @Override
    public FriendLink getById(Long id) {
        return friendLinkMapper.selectById(id);
    }
}