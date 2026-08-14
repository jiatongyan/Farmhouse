package com.dylan.farmhouse.user.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录结果。
 */
@Data
@Builder
public class LoginVO {

    private Long userId;

    private String username;

    private String nickname;

    private Integer role;

    private String token;
}
