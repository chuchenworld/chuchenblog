package com.chuchen.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Album;
import com.chuchen.blog.entity.Photo;
import com.chuchen.blog.entity.User;
import com.chuchen.blog.mapper.AlbumMapper;
import com.chuchen.blog.mapper.PhotoMapper;
import com.chuchen.blog.mapper.UserMapper;
import com.chuchen.blog.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 照片Service实现类
 */
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final PhotoMapper photoMapper;
    private final AlbumMapper albumMapper;
    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(PhotoServiceImpl.class);

    @Override
    public Result<Page<Photo>> getPhotos(int pageNum, int pageSize) {
        logger.info("【PhotoServiceImpl】开始获取照片列表，页码: {}, 每页数量: {}", pageNum, pageSize);
        try {
            Page<Photo> page = new Page<>(pageNum, pageSize);
            QueryWrapper<Photo> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("shoot_time").orderByDesc("create_time");
            photoMapper.selectPage(page, queryWrapper);
            logger.info("【PhotoServiceImpl】照片列表获取成功，总数: {}", page.getTotal());
            return Result.success(page);
        } catch (Exception e) {
            logger.error("【PhotoServiceImpl】照片列表获取失败: {}", e.getMessage());
            return Result.error("获取照片列表失败");
        }
    }

    @Override
    public Result<Photo> getPhotoById(Long id) {
        logger.info("【PhotoServiceImpl】开始获取照片详情，ID: {}", id);
        try {
            Photo photo = photoMapper.selectById(id);
            if (photo == null) {
                logger.warn("【PhotoServiceImpl】照片不存在，ID: {}", id);
                return Result.error(404, "照片不存在");
            }
            logger.info("【PhotoServiceImpl】照片详情获取成功，名称: {}", photo.getName());
            return Result.success(photo);
        } catch (Exception e) {
            logger.error("【PhotoServiceImpl】照片详情获取失败: {}", e.getMessage());
            return Result.error("获取照片详情失败");
        }
    }

    @Override
    @Transactional
    public Result<Void> createPhoto(Photo photo) {
        logger.info("【PhotoServiceImpl】开始创建照片，名称: {}", photo.getName());
        try {
            // 获取当前登录用户
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
            if (user == null) {
                logger.warn("【PhotoServiceImpl】当前用户不存在");
                return Result.error(401, "用户未登录或不存在");
            }
            
            photo.setCreateTime(LocalDateTime.now());
            photo.setUpdateTime(LocalDateTime.now());
            photo.setUserId(user.getId());
            
            // 处理 tags 字段：空列表转换为 null（JSON类型字段不能存空字符串）
            if (photo.getTags() != null && photo.getTags().isEmpty()) {
                photo.setTags(null);
            }
            
            // 如果设置了相册ID，获取相册名称
            if (photo.getAlbumId() != null) {
                Album album = albumMapper.selectById(photo.getAlbumId());
                if (album == null) {
                    logger.warn("【PhotoServiceImpl】相册不存在，ID: {}", photo.getAlbumId());
                    return Result.error(404, "相册不存在");
                }
                // 确保相册名称不为空
                if (album.getName() != null && !album.getName().isEmpty()) {
                    photo.setAlbumName(album.getName());
                } else {
                    photo.setAlbumName("未命名相册");
                }
            }
                
            // 执行数据库插入
            int insertResult = photoMapper.insert(photo);
            if (insertResult > 0) {
                logger.info("【PhotoServiceImpl】照片创建成功，ID: {}, 用户ID: {}", photo.getId(), user.getId());
                return Result.success();
            } else {
                logger.error("【PhotoServiceImpl】照片创建失败：数据库插入返回0");
                return Result.error("创建照片失败");
            }
        } catch (Exception e) {
            logger.error("【PhotoServiceImpl】照片创建失败: {}", e.getMessage(), e);
            return Result.error("创建照片失败");
        }
    }

    @Override
    @Transactional
    public Result<Void> updatePhoto(Long id, Photo photo) {
        logger.info("【PhotoServiceImpl】开始更新照片，ID: {}", id);
        try {
            Photo existing = photoMapper.selectById(id);
            if (existing == null) {
                logger.warn("【PhotoServiceImpl】照片不存在，ID: {}", id);
                return Result.error(404, "照片不存在");
            }
            
            if (photo.getName() != null) {
                existing.setName(photo.getName());
            }
            if (photo.getUrl() != null) {
                existing.setUrl(photo.getUrl());
            }
            if (photo.getDescription() != null) {
                existing.setDescription(photo.getDescription());
            }
            if (photo.getCaption() != null) {
                existing.setCaption(photo.getCaption());
            }
            if (photo.getTags() != null) {
                // 处理 tags 字段：空列表转换为 null（JSON类型字段不能存空字符串）
                existing.setTags(photo.getTags().isEmpty() ? null : photo.getTags());
            }
            if (photo.getShootTime() != null) {
                existing.setShootTime(photo.getShootTime());
            }
            
            // 处理相册关联变更
            if (photo.getAlbumId() != null) {
                Album album = albumMapper.selectById(photo.getAlbumId());
                if (album == null) {
                    logger.warn("【PhotoServiceImpl】相册不存在，ID: {}", photo.getAlbumId());
                    return Result.error(404, "相册不存在");
                }
                existing.setAlbumId(photo.getAlbumId());
                // 确保相册名称不为空
                if (album.getName() != null && !album.getName().isEmpty()) {
                    existing.setAlbumName(album.getName());
                } else {
                    existing.setAlbumName("未命名相册");
                }
            } else if (photo.getAlbumId() == null && existing.getAlbumId() != null) {
                // 如果设置为null，解除相册关联
                existing.setAlbumId(null);
                existing.setAlbumName(null);
            }
            
            existing.setUpdateTime(LocalDateTime.now());
            
            // 执行数据库更新
            int updateResult = photoMapper.updateById(existing);
            if (updateResult > 0) {
                logger.info("【PhotoServiceImpl】照片更新成功，ID: {}", id);
                return Result.success();
            } else {
                logger.error("【PhotoServiceImpl】照片更新失败：数据库更新返回0");
                return Result.error("更新照片失败");
            }
        } catch (Exception e) {
            logger.error("【PhotoServiceImpl】照片更新失败: {}", e.getMessage(), e);
            return Result.error("更新照片失败");
        }
    }

    @Override
    @Transactional
    public Result<Void> deletePhoto(Long id) {
        logger.info("【PhotoServiceImpl】开始删除照片，ID: {}", id);
        try {
            Photo photo = photoMapper.selectById(id);
            if (photo == null) {
                logger.warn("【PhotoServiceImpl】照片不存在，ID: {}", id);
                return Result.error(404, "照片不存在");
            }
            photoMapper.deleteById(id);
            logger.info("【PhotoServiceImpl】照片删除成功，ID: {}", id);
            return Result.success();
        } catch (Exception e) {
            logger.error("【PhotoServiceImpl】照片删除失败: {}", e.getMessage());
            return Result.error("删除照片失败");
        }
    }

    @Override
    public Result<Page<Photo>> getPhotosByAlbum(Long albumId, int pageNum, int pageSize) {
        logger.info("【PhotoServiceImpl】开始获取相册照片，相册ID: {}, 页码: {}, 每页数量: {}", albumId, pageNum, pageSize);
        try {
            // 先验证相册是否存在
            Album album = albumMapper.selectById(albumId);
            if (album == null) {
                logger.warn("【PhotoServiceImpl】相册不存在，ID: {}", albumId);
                return Result.error(404, "相册不存在");
            }
            
            Page<Photo> page = new Page<>(pageNum, pageSize);
            QueryWrapper<Photo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("album_id", albumId);
            queryWrapper.orderByDesc("shoot_time").orderByDesc("create_time");
            photoMapper.selectPage(page, queryWrapper);
            logger.info("【PhotoServiceImpl】相册照片获取成功，相册ID: {}, 照片总数: {}", albumId, page.getTotal());
            return Result.success(page);
        } catch (Exception e) {
            logger.error("【PhotoServiceImpl】相册照片获取失败: {}", e.getMessage());
            return Result.error("获取相册照片失败");
        }
    }
}
