package com.chuchen.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.dto.RegisterRequest;
import com.chuchen.blog.dto.UserDTO;
import com.chuchen.blog.entity.User;
import com.chuchen.blog.mapper.UserMapper;
import com.chuchen.blog.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public Result<Void> register(RegisterRequest request) {
        User existingUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getAccount()));

        if (existingUser != null) {
            return Result.error(400, "账号已存在");
        }

        User user = new User();
        user.setUsername(request.getAccount());
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getAccount().contains("@")) {
            user.setEmail(request.getAccount());
        }
        user.setRole("user");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());

        userMapper.insert(user);
        return Result.success();
    }

    public Result<IPage<UserVO>> getUsers(int page, int size, String keyword) {
        Page<User> userPage = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like(User::getUsername, keyword);
        }

        IPage<User> pageResult = userMapper.selectPage(userPage, queryWrapper);

        IPage<UserVO> userVOPage = pageResult.convert(user -> {
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setEmail(user.getEmail());
            userVO.setRole(user.getRole());
            userVO.setStatus(user.getStatus());
            userVO.setCreateTime(user.getCreateTime());
            return userVO;
        });

        return Result.success(userVOPage);
    }

    public Result<Void> createUser(UserDTO userDTO) {
        User existingUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userDTO.getUsername()));

        if (existingUser != null) {
            return Result.error(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setStatus(userDTO.getStatus());
        user.setCreateTime(LocalDateTime.now());

        userMapper.insert(user);
        return Result.success();
    }

    public Result<Void> updateUser(Long id, UserDTO userDTO) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        if (!user.getUsername().equals(userDTO.getUsername())) {
            User existingUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, userDTO.getUsername()));
            if (existingUser != null) {
                return Result.error(400, "用户名已存在");
            }
        }

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setStatus(userDTO.getStatus());

        userMapper.updateById(user);
        return Result.success();
    }

    public Result<Void> resetPassword(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        user.setPassword(passwordEncoder.encode("123456"));
        userMapper.updateById(user);
        return Result.success();
    }

    public Result<Void> deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        userMapper.deleteById(id);
        return Result.success();
    }

    public User getUserByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    public User getUserByRole(String role) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getRole, role)
                .orderByAsc(User::getId)
                .last("LIMIT 1"));
    }

    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    public User getFirstAdmin() {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "admin")
                .orderByAsc(User::getId)
                .last("LIMIT 1"));
    }
}