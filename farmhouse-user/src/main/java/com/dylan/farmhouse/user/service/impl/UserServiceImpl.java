package com.dylan.farmhouse.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dylan.farmhouse.common.exception.BizException;
import com.dylan.farmhouse.common.result.ResultCode;
import com.dylan.farmhouse.common.util.BcryptUtil;
import com.dylan.farmhouse.common.util.JwtUtil;
import com.dylan.farmhouse.user.dto.LoginDTO;
import com.dylan.farmhouse.user.dto.RegisterDTO;
import com.dylan.farmhouse.user.entity.User;
import com.dylan.farmhouse.user.mapper.UserMapper;
import com.dylan.farmhouse.user.service.UserService;
import com.dylan.farmhouse.user.vo.LoginVO;
import com.dylan.farmhouse.user.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public void register(RegisterDTO dto) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count != null && count > 0) {
            throw new BizException(ResultCode.USERNAME_EXIST);
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BcryptUtil.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname());
        user.setRole(dto.getRole() == null ? 0 : dto.getRole());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null || !BcryptUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .token(token)
                .build();
    }

    @Override
    public UserInfoVO info(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }
        return UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }
}
