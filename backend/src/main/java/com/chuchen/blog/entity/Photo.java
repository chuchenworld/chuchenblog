package com.chuchen.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 照片实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "photo", autoResultMap = true)
public class Photo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 照片名称
     */
    private String name;

    /**
     * 照片URL
     */
    private String url;

    /**
     * 描述
     */
    private String description;

    /**
     * 配文（简短文字描述）
     */
    private String caption;

    /**
     * 标签（JSON数组格式）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 拍摄时间
     */
    private LocalDateTime shootTime;

    /**
     * 所属相册ID
     */
    private Long albumId;

    /**
     * 所属相册名称（冗余字段，用于显示）
     */
    @TableField(exist = false)
    private String albumName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
