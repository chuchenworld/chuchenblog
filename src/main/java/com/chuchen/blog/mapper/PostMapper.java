package com.chuchen.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuchen.blog.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}