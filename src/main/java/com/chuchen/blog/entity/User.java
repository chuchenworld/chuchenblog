package com.chuchen.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String bio;
    private String tags;
    private String password;
    private String email;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
}