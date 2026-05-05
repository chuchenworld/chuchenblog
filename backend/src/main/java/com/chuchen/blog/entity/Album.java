package com.chuchen.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 相册实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("album")
public class Album {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 相册名称
     */
    private String name;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 照片数量
     */
    @TableField(exist = false)
    private Integer photoCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
