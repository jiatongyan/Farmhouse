package com.dylan.farmhouse.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表。
 */
@Data
@TableName("`user`")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String phone;

    private String nickname;

    /** 角色：0 消费者，1 商户 */
    private Integer role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
