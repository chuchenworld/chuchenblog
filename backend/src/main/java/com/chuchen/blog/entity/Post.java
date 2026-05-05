package com.chuchen.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("post" )
public class Post {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Integer status;
    private Integer viewCount;
    private Long userId;
    private Long categoryId;
    private LocalDateTime createTime;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    @TableField(exist = false)
    private String authorName;

    @TableField(exist = false)
    private Integer commentCount;
}
