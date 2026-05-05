package com.chuchen.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Album;
import com.chuchen.blog.entity.Photo;
import com.chuchen.blog.entity.User;
import com.chuchen.blog.mapper.AlbumMapper;
import com.chuchen.blog.mapper.PhotoMapper;
import com.chuchen.blog.mapper.UserMapper;
import com.chuchen.blog.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 相册Service实现类
 */
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumMapper albumMapper;
    private final PhotoMapper photoMapper;
    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(AlbumServiceImpl.class);

    @Override
    public Result<List<Album>> getAlbums() {
        logger.info("【AlbumServiceImpl】开始获取相册列表");
        try {
            List<Album> albums = albumMapper.selectList(null);
            for (Album album : albums) {
                Long count = photoMapper.selectCount(new QueryWrapper<Photo>().eq("album_id", album.getId()));
                album.setPhotoCount(count.intValue());
            }
            logger.info("【AlbumServiceImpl】相册列表获取成功，数量: {}", albums.size());
            return Result.success(albums);
        } catch (Exception e) {
            logger.error("【AlbumServiceImpl】相册列表获取失败: {}", e.getMessage());
            return Result.error("获取相册列表失败");
        }
    }
    @Override
    public Result<Album> getAlbumById(Long id) {
        logger.info("【AlbumServiceImpl】开始获取相册详情，ID: {}", id);
        try {
            Album album = albumMapper.selectById(id);
            if (album == null) {
                logger.warn("【AlbumServiceImpl】相册不存在，ID: {}", id);
                return Result.error("相册不存在");
            }
            Long count = photoMapper.selectCount(new QueryWrapper<Photo>().eq("album_id", id));
            album.setPhotoCount(count.intValue());
            logger.info("【AlbumServiceImpl】相册详情获取成功，名称: {}", album.getName());
            return Result.success(album);
        } catch (Exception e) {
            logger.error("【AlbumServiceImpl】相册详情获取失败: {}", e.getMessage());
            return Result.error("获取相册详情失败");
        }
    }


    @Override
    @Transactional
    public Result<Void> createAlbum(Album album) {
        logger.info("【AlbumServiceImpl】开始创建相册，名称: {}", album.getName());
        try {
            // 获取当前登录用户
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
            if (user == null) {
                logger.warn("【AlbumServiceImpl】当前用户不存在");
                return Result.error(401, "用户未登录或不存在");
            }
            
            // 校验相册名称
            if (album.getName() == null || album.getName().isEmpty()) {
                logger.warn("【AlbumServiceImpl】相册名称不能为空");
                return Result.error(400, "相册名称不能为空");
            }
            
            // 校验封面URL（允许为空，但如果不为空则校验格式）
            if (album.getCoverUrl() != null && !album.getCoverUrl().isEmpty() && !album.getCoverUrl().startsWith("http")) {
                logger.warn("【AlbumServiceImpl】封面URL格式不正确");
                return Result.error(400, "封面URL格式不正确");
            }
            
            album.setCreateTime(LocalDateTime.now());
            album.setUpdateTime(LocalDateTime.now());
            album.setPhotoCount(0);
            album.setUserId(user.getId());
            albumMapper.insert(album);
            logger.info("【AlbumServiceImpl】相册创建成功，ID: {}, 创建人ID: {}", album.getId(), user.getId());
            return Result.success();
        } catch (Exception e) {
            logger.error("【AlbumServiceImpl】相册创建失败: {}", e.getMessage(), e);
            return Result.error("创建相册失败");
        }
    }

    @Override
    @Transactional
    public Result<Void> updateAlbum(Long id, Album album) {
        logger.info("【AlbumServiceImpl】开始更新相册，ID: {}, 名称: {}", id, album.getName());
        try {
            Album existing = albumMapper.selectById(id);
            if (existing == null) {
                logger.warn("【AlbumServiceImpl】相册不存在，ID: {}", id);
                return Result.error("相册不存在");
            }
            if (album.getName() != null) {
                existing.setName(album.getName());
            }
            if (album.getCoverUrl() != null) {
                existing.setCoverUrl(album.getCoverUrl());
            }
            existing.setUpdateTime(LocalDateTime.now());
            albumMapper.updateById(existing);
            logger.info("【AlbumServiceImpl】相册更新成功，ID: {}", id);
            return Result.success();
        } catch (Exception e) {
            logger.error("【AlbumServiceImpl】相册更新失败: {}", e.getMessage());
            return Result.error("更新相册失败");
        }
    }

    @Override
    @Transactional
    public Result<Void> deleteAlbum(Long id) {
        logger.info("【AlbumServiceImpl】开始删除相册，ID: {}", id);
        try {
            Album album = albumMapper.selectById(id);
            if (album == null) {
                logger.warn("【AlbumServiceImpl】相册不存在，ID: {}", id);
                return Result.error("相册不存在");
            }
            // 先解除照片与相册的关联（不删除照片）
            QueryWrapper<Photo> photoQuery = new QueryWrapper<>();
            photoQuery.eq("album_id", id);
            List<Photo> photos = photoMapper.selectList(photoQuery);
            for (Photo photo : photos) {
                photo.setAlbumId(null);
                photo.setAlbumName(null);
                photoMapper.updateById(photo);
            }
            // 删除相册
            albumMapper.deleteById(id);
            logger.info("【AlbumServiceImpl】相册删除成功，ID: {}", id);
            return Result.success();
        } catch (Exception e) {
            logger.error("【AlbumServiceImpl】相册删除失败: {}", e.getMessage());
            return Result.error("删除相册失败");
        }
    }
}
