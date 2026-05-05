package com.chuchen.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Long photoId;
    private Long userId;
    private String nickname;
    private String email;
    private String content;
    private Boolean isAnonymous;
    private Integer likeCount;
    @TableField(exist = false)
    private Boolean notifyAuthor;
    private LocalDateTime createTime;
    private Long parentId;
}
