package com.dylan.farmhouse.user.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 用户信息。
 */
@Data
@Builder
public class UserInfoVO {

    private Long id;

    private String username;

    private String nickname;

    private String phone;

    private Integer role;
}
