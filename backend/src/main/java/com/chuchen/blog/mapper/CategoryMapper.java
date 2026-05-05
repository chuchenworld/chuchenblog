package com.chuchen.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuchen.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}