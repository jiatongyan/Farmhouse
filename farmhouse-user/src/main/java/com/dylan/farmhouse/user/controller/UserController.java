package com.dylan.farmhouse.user.controller;

import com.dylan.farmhouse.common.result.Result;
import com.dylan.farmhouse.user.dto.LoginDTO;
import com.dylan.farmhouse.user.dto.RegisterDTO;
import com.dylan.farmhouse.user.service.UserService;
import com.dylan.farmhouse.user.vo.LoginVO;
import com.dylan.farmhouse.user.vo.UserInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @GetMapping("/info")
    public Result<UserInfoVO> info(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(userService.info(userId));
    }
}
