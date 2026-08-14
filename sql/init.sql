CREATE DATABASE IF NOT EXISTS farmhouse DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE farmhouse;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`         bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`   varchar(50)  NOT NULL COMMENT '用户名',
    `password`   varchar(100) NOT NULL COMMENT '加密密码',
    `phone`      varchar(20)  DEFAULT NULL COMMENT '手机号',
    `nickname`   varchar(50)  DEFAULT NULL COMMENT '昵称',
    `role`       tinyint      NOT NULL DEFAULT '0' COMMENT '角色：0消费者 1商户',
    `created_at` datetime     DEFAULT NULL COMMENT '创建时间',
    `updated_at` datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';
