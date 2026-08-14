package com.dylan.farmhouse.user.service;

import com.dylan.farmhouse.user.dto.LoginDTO;
import com.dylan.farmhouse.user.dto.RegisterDTO;
import com.dylan.farmhouse.user.vo.LoginVO;
import com.dylan.farmhouse.user.vo.UserInfoVO;

public interface UserService {

    void register(RegisterDTO dto);

    LoginVO login(LoginDTO dto);

    UserInfoVO info(Long userId);
}
