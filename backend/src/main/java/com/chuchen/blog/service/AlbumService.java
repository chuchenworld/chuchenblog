package com.chuchen.blog.service;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Album;

import java.util.List;

/**
 * 相册Service接口
 */
public interface AlbumService {

    /**
     * 获取所有相册列表
     */
    Result<List<Album>> getAlbums();

    /**
     * 根据ID获取相册详情
     */
    Result<Album> getAlbumById(Long id);

    /**
     * 创建相册
     */
    Result<Void> createAlbum(Album album);

    /**
     * 更新相册
     */
    Result<Void> updateAlbum(Long id, Album album);

    /**
     * 删除相册（不删除关联的照片）
     */
    Result<Void> deleteAlbum(Long id);
}
