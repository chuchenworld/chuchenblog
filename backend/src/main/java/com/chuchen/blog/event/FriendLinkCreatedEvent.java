package com.chuchen.blog.event;

import com.chuchen.blog.entity.FriendLink;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 友链创建事件
 */
@Getter
public class FriendLinkCreatedEvent extends ApplicationEvent {

    private final FriendLink friendLink;

    public FriendLinkCreatedEvent(FriendLink friendLink) {
        super(friendLink);
        this.friendLink = friendLink;
    }
}