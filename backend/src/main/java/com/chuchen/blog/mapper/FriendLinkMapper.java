package com.chuchen.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuchen.blog.entity.FriendLink;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 友情链接Mapper接口
 */
@Mapper
public interface FriendLinkMapper extends BaseMapper<FriendLink> {

    /**
     * 查询所有已通过审核的友链
     */
    List<FriendLink> selectApprovedLinks();

    /**
     * 查询所有友链（包含待审核）
     */
    List<FriendLink> selectAllLinks();
}