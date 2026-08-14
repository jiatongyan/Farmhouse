package com.dylan.farmhouse.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商户/店铺表。
 */
@Data
@TableName("merchant")
public class Merchant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String shopName;

    /** 状态：1 营业中，2 休息中，3 关闭 */
    private Integer status;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
