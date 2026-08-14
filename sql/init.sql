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

-- 商户/店铺表
CREATE TABLE IF NOT EXISTS `merchant` (
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint       NOT NULL COMMENT '关联用户ID',
    `shop_name`   varchar(100) DEFAULT NULL COMMENT '店铺名称',
    `status`      tinyint      NOT NULL DEFAULT '1' COMMENT '1营业中 2休息中 3关闭',
    `description` varchar(500) DEFAULT NULL COMMENT '店铺描述',
    `created_at`  datetime     DEFAULT NULL COMMENT '创建时间',
    `updated_at`  datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商户店铺表';

-- 农家乐服务表
CREATE TABLE IF NOT EXISTS `product` (
    `id`          bigint         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `merchant_id` bigint         NOT NULL COMMENT '商户ID',
    `category_id` bigint         DEFAULT NULL COMMENT '分类ID',
    `title`       varchar(100)   NOT NULL COMMENT '服务名称',
    `subtitle`    varchar(200)   DEFAULT NULL COMMENT '副标题',
    `description` text           COMMENT '服务描述',
    `price`       decimal(10, 2) NOT NULL COMMENT '价格',
    `cover_url`   varchar(255)   DEFAULT NULL COMMENT '封面图',
    `stock`       int            NOT NULL DEFAULT '0' COMMENT '剩余库存(临时，后续库存模块接管)',
    `status`      tinyint        NOT NULL DEFAULT '0' COMMENT '0草稿 1已上架 2已下架',
    `created_at`  datetime       DEFAULT NULL COMMENT '创建时间',
    `updated_at`  datetime       DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '农家乐服务表';
