package com.dylan.farmhouse.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 农家乐服务表。
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;

    private Long categoryId;

    private String title;

    private String subtitle;

    private String description;

    private BigDecimal price;

    private String coverUrl;

    /** 剩余库存（临时放在服务表，后续由库存模块接管） */
    private Integer stock;

    /** 状态：0 草稿，1 已上架，2 已下架 */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
