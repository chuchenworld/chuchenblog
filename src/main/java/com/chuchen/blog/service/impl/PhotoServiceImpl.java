package com.chuchen.blog.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Photo;
import com.chuchen.blog.mapper.PhotoMapper;
import com.chuchen.blog.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final PhotoMapper photoMapper;

    @Override
    public Result<Page<Photo>> getPhotos(int pageNum, int pageSize) {
        Page<Photo> page = new Page<>(pageNum, pageSize);
        photoMapper.selectPage(page, null);
        return Result.success(page);
    }

    @Override
    public Result<Photo> getPhotoById(Long id) {
        Photo photo = photoMapper.selectById(id);
        if (photo == null) {
            return Result.error(404, "照片不存在");
        }
        return Result.success(photo);
    }

    @Override
    public Result<Void> createPhoto(Photo photo) {
        photoMapper.insert(photo);
        return Result.success();
    }

    @Override
    public Result<Void> updatePhoto(Long id, Photo photo) {
        photo.setId(id);
        photoMapper.updateById(photo);
        return Result.success();
    }

    @Override
    public Result<Void> deletePhoto(Long id) {
        photoMapper.deleteById(id);
        return Result.success();
    }
}
