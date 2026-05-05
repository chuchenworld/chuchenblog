package com.chuchen.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 友情链接实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("friend_link")
public class FriendLink {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 站点名称
     */
    @TableField("site_name")
    private String siteName;

    /**
     * 站点描述
     */
    @TableField("site_description")
    private String siteDescript;

    /**
     * 站点链接
     */
    @TableField("site_url")
    private String siteUrl;

    /**
     * 头像链接
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 状态：0-待审核，1-已通过，2-已停用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}