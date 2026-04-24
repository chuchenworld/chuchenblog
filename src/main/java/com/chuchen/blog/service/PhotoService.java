package com.chuchen.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Photo;

public interface PhotoService {
    Result<Page<Photo>> getPhotos(int pageNum, int pageSize);
    Result<Photo> getPhotoById(Long id);
    Result<Void> createPhoto(Photo photo);
    Result<Void> updatePhoto(Long id, Photo photo);
    Result<Void> deletePhoto(Long id);
}
