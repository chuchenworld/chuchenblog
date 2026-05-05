package com.chuchen.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuchen.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    List<Comment> findByArticleId(@Param("articleId") Long articleId);

    @Select("SELECT COUNT(*) FROM comment WHERE article_id = #{articleId}")
    Long countByArticleId(@Param("articleId") Long articleId);
    
    @Select("SELECT c.* FROM comment c WHERE c.photo_id = #{photoId} ORDER BY c.create_time DESC")
    List<Comment> findByPhotoId(@Param("photoId") Long photoId);
    
    @Select("SELECT COUNT(*) FROM comment WHERE photo_id = #{photoId}")
    Long countByPhotoId(@Param("photoId") Long photoId);
    
    @Update("UPDATE comment SET like_count = like_count + 1 WHERE id = #{commentId}")
    int incrementLikeCount(@Param("commentId") Long commentId);
}
